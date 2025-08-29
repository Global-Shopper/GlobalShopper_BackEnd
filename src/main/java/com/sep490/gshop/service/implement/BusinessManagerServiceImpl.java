package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.BusinessManagerBusiness;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.Configuration;
import com.sep490.gshop.entity.Order;
import com.sep490.gshop.entity.OrderItem;
import com.sep490.gshop.payload.dto.ConfigurationDTO;
import com.sep490.gshop.payload.dto.CustomerDTO;
import com.sep490.gshop.payload.request.bm.ServiceFeeConfigModel;
import com.sep490.gshop.payload.response.dashboard.*;
import com.sep490.gshop.payload.response.subclass.PRStatus;
import com.sep490.gshop.service.BusinessManagerService;
import com.sep490.gshop.utils.CalculationUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@Log4j2
public class BusinessManagerServiceImpl implements BusinessManagerService {

    private final BusinessManagerBusiness businessManagerBusiness;
    private final ModelMapper modelMapper;
    private final CalculationUtil calculationUtil;

    @Autowired
    public BusinessManagerServiceImpl(BusinessManagerBusiness businessManagerBusiness, ModelMapper modelMapper, CalculationUtil calculationUtil) {
        this.businessManagerBusiness = businessManagerBusiness;
        this.modelMapper = modelMapper;
        this.calculationUtil = calculationUtil;
    }


    @Override
    public ConfigurationDTO updateServiceFee(ServiceFeeConfigModel serviceFee) {
        try {
            log.info("updateServiceFee() BusinessManagerController Start");
            Configuration config = businessManagerBusiness.updateServiceFee(serviceFee.getServiceFee());
            ConfigurationDTO updatedBusinessManager = modelMapper.map(config, ConfigurationDTO.class);
            log.info("updateServiceFee() BusinessManagerController End | updatedBusinessManager: {}", updatedBusinessManager);
            return updatedBusinessManager;
        } catch (Exception e) {
            log.error("Error updating service fee: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ConfigurationDTO getBusinessManagerConfig() {
        try {
            log.info("getBusinessManagerConfig() BusinessManagerController Start");
            Configuration config = businessManagerBusiness.getConfig();
            ConfigurationDTO updatedBusinessManager = modelMapper.map(config, ConfigurationDTO.class);
            log.info("getBusinessManagerConfig() BusinessManagerController End | updatedBusinessManager: {}", updatedBusinessManager);
            return updatedBusinessManager;
        } catch (Exception e) {
            log.error("getBusinessManagerConfig() BusinessManagerController error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Page<CustomerDTO> getCustomer(Pageable pageable, String search, Boolean status, Long startDate, Long endDate) {
        try {
            log.info("getBusinessManagerUser() BusinessManagerController Start");
            Page<CustomerDTO> user = businessManagerBusiness.getCustomer(pageable, search, status, startDate, endDate);
            log.info("getBusinessManagerUser() BusinessManagerController End | user: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Error getting customer: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public DashBoardResponse getDashboard(Long startDate, Long endDate) {
        try {
            log.info("getPrDashboard() BusinessManagerController Start | startDate: {}, endDate: {}", startDate, endDate);
            DashBoardResponse prDashBoard = businessManagerBusiness.getDashboard(startDate, endDate);
            log.info("getPrDashboard() BusinessManagerController End | prDashBoard: {}", prDashBoard);
            return prDashBoard;
        } catch (Exception e) {
            log.error("Error getting PR dashboard: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public RevenueResponse getRevenue(Long startDate, Long endDate) {
        try {
            log.info("getRevenue() BusinessManagerController Start | startDate: {}, endDate: {}", startDate, endDate);
            List<Order> orders = businessManagerBusiness.getRevenue(startDate, endDate);
            RevenueResponse revenue = calculateTotalRevenue(orders);
            log.info("getRevenue() BusinessManagerController End | revenue: {}", revenue);
            return revenue;
        } catch (Exception e) {
            log.error("Error getting revenue: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public List<MonthlyRevenue> getRevenueSummaryByMonth(int year) {
        List<MonthlyRevenue> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth())
                    .withHour(23).withMinute(59).withSecond(59);

            long startEpoch = startOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endEpoch   = endOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            List<Order> orders = businessManagerBusiness.getRevenue(startEpoch, endEpoch);
            RevenueResponse rr = calculateTotalRevenue(orders);

            result.add(new MonthlyRevenue(month, rr.getTotal(), rr.getTotalOnline(), rr.getTotalOffline()));
        }

        return result;
    }


    @Override
    public List<MonthlyDashboard> getDashboardByYear(int year) {
        List<MonthlyDashboard> result = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth())
                    .withHour(23).withMinute(59).withSecond(59);

            long startEpoch = startOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endEpoch   = endOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // tái sử dụng hàm getDashboard(startDate, endDate)
            DashBoardResponse dashboard = getDashboard(startEpoch, endEpoch);

            result.add(new MonthlyDashboard(month, dashboard));
        }

        return result;
    }



    @Override
    public byte[] exportRevenueByMonth(int year) throws IOException {
        int currentYear = Year.now().getValue();
        if (year > currentYear) {
            throw AppException.builder().message("Không thể xuất dữ liệu của năm lớn hơn năm hiện tại (" + currentYear + ")").code(400).build();
        }


        List<MonthlyRevenue> revenues = getRevenueSummaryByMonth(year);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheetRevenue = workbook.createSheet("Revenue " + year);

        Row header = sheetRevenue.createRow(0);
        header.createCell(0).setCellValue("Month");
        header.createCell(1).setCellValue("Total");
        header.createCell(2).setCellValue("Online");
        header.createCell(3).setCellValue("Offline");

        for (int i = 0; i < revenues.size(); i++) {
            MonthlyRevenue r = revenues.get(i);
            Row row = sheetRevenue.createRow(i + 1);
            row.createCell(0).setCellValue("T"+r.getMonth());
            row.createCell(1).setCellValue(r.getTotal());
            row.createCell(2).setCellValue(r.getOnline());
            row.createCell(3).setCellValue(r.getOffline());
        }


        XSSFDrawing drawing1 = sheetRevenue.createDrawingPatriarch();
        XSSFClientAnchor anchor1 = drawing1.createAnchor(0, 0, 0, 0, 5, 1, 15, 20);
        XSSFChart chart1 = drawing1.createChart(anchor1);
        chart1.setTitleText("Tổng doanh thu theo tháng của năm " + year);
        chart1.setTitleOverlay(false);

        XDDFCategoryAxis bottomAxis1 = chart1.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis1.setTitle("Tháng");
        XDDFValueAxis leftAxis1 = chart1.createValueAxis(AxisPosition.LEFT);
        leftAxis1.setTitle("VNĐ");

        XDDFDataSource<String> months = XDDFDataSourcesFactory.fromStringCellRange(
                sheetRevenue, new CellRangeAddress(1, revenues.size(), 0, 0));
        XDDFNumericalDataSource<Double> totals = XDDFDataSourcesFactory.fromNumericCellRange(
                sheetRevenue, new CellRangeAddress(1, revenues.size(), 1, 1));

        XDDFLineChartData lineData = (XDDFLineChartData) chart1.createData(ChartTypes.LINE, bottomAxis1, leftAxis1);

        XDDFLineChartData.Series seriesTotal = (XDDFLineChartData.Series) lineData.addSeries(months, totals);
        seriesTotal.setTitle("Tổng doanh thu", null);
        seriesTotal.setSmooth(true);
        seriesTotal.setMarkerStyle(MarkerStyle.CIRCLE);
        leftAxis1.setCrosses(AxisCrosses.AUTO_ZERO);
        leftAxis1.setMinimum(0.0);
        chart1.plot(lineData);


        chart1.getOrAddLegend().setPosition(LegendPosition.RIGHT);
        XSSFDrawing drawing2 = sheetRevenue.createDrawingPatriarch();
        XSSFClientAnchor anchor2 = drawing2.createAnchor(0, 0, 0, 0, 16, 1, 26, 20);
        XSSFChart chart2 = drawing2.createChart(anchor2);
        chart2.setTitleText("Doanh thu loại đơn hàng theo tháng của năm " + year);
        chart2.setTitleOverlay(false);

        XDDFCategoryAxis bottomAxis2 = chart2.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis2.setTitle("Tháng");
        XDDFValueAxis leftAxis2 = chart2.createValueAxis(AxisPosition.LEFT);
        leftAxis2.setTitle("VNĐ");

        XDDFNumericalDataSource<Double> onlineValues = XDDFDataSourcesFactory.fromNumericCellRange(
                sheetRevenue, new CellRangeAddress(1, revenues.size(), 2, 2));
        XDDFNumericalDataSource<Double> offlineValues = XDDFDataSourcesFactory.fromNumericCellRange(
                sheetRevenue, new CellRangeAddress(1, revenues.size(), 3, 3));

        XDDFChartData barData = chart2.createData(ChartTypes.BAR, bottomAxis2, leftAxis2);
        ((XDDFBarChartData) barData).setBarDirection(BarDirection.COL);


        XDDFChartData.Series seriesOnline = barData.addSeries(months, onlineValues);
        seriesOnline.setTitle("Online", null);

        XDDFChartData.Series seriesOffline = barData.addSeries(months, offlineValues);
        seriesOffline.setTitle("Offline", null);

        chart2.plot(barData);
        chart2.getOrAddLegend().setPosition(LegendPosition.RIGHT);
        List<MonthlyDashboard> dashboards = getDashboardByYear(year);
        XSSFSheet sheetDashBoard = workbook.createSheet("Dashboard " + year);
        int row = 0;
        row = addDashboardBlockByMonth(workbook, sheetDashBoard, row, "PurchaseRequest", dashboards, "PurchaseRequest");
        row = addDashboardBlockByMonth(workbook, sheetDashBoard, row, "Order", dashboards, "Order");
        row = addDashboardBlockByMonth(workbook, sheetDashBoard, row, "RefundTicket", dashboards, "RefundTicket");
        row = addDashboardBlockByMonth(workbook, sheetDashBoard, row, "WithdrawTicket", dashboards, "WithdrawTicket");


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        return bos.toByteArray();
    }





    private RevenueResponse calculateTotalRevenue(List<Order> orders) {
        double totalOnline = 0.0;
        double totalOffline = 0.0;
        double total = 0.0;
        for (Order o : orders) {
            double serviceFeeSum = o.getOrderItems()
                    .stream()
                    .mapToDouble(OrderItem::getServiceFee)
                    .sum();

            double vnd = calculationUtil
                    .convertToVND(BigDecimal.valueOf(serviceFeeSum), o.getCurrency())
                    .doubleValue();
            total = total + vnd;
            if (RequestType.ONLINE.equals(o.getType())) {
                totalOnline += vnd;
            } else if (RequestType.OFFLINE.equals(o.getType())) { // OFFLINE
                totalOffline += vnd;
            }
        }
        return new RevenueResponse(total, totalOnline, totalOffline);
    }



    private int addDashboardBlockByMonth(XSSFWorkbook workbook,
                                         XSSFSheet sheet,
                                         int startRow,
                                         String blockTitle,
                                         List<MonthlyDashboard> dashboards,
                                         String blockName) {
        Map<String, Double[]> statusSeries = new LinkedHashMap<>();

        // --- Thu thập dữ liệu ---
        for (MonthlyDashboard m : dashboards) {
            for (DashBoardDetail d : m.getDashboard().getDashBoardList()) {
                if (blockName.equals(d.getDashBoardName())) {
                    for (PRStatus st : d.getStatusList()) {
                        statusSeries.computeIfAbsent(st.getStatus(), k -> new Double[12]);
                        statusSeries.get(st.getStatus())[m.getMonth() - 1] =
                                (st.getCount().doubleValue() == 0 ? null : st.getCount().doubleValue());
                    }
                }
            }
        }
        if (statusSeries.isEmpty()) {
            statusSeries.put("No Data", new Double[12]);
        }

        // --- Title ---
        Row titleRow = sheet.createRow(startRow);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(blockTitle);

        if (statusSeries.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, statusSeries.size() - 1));
        }

        // --- Header ---
        int pivotStartRow = startRow + 1;
        Row pivotHeader = sheet.createRow(pivotStartRow);
        pivotHeader.createCell(0).setCellValue("Month\\Status");

        int col = 1;
        for (String status : statusSeries.keySet()) {
            pivotHeader.createCell(col++).setCellValue(status);
        }

        // --- Data ---
        for (int month = 1; month <= 12; month++) {
            Row r = sheet.createRow(pivotStartRow + month);
            r.createCell(0).setCellValue("T" + month);
            col = 1;
            for (Double[] values : statusSeries.values()) {
                Cell c = r.createCell(col++);
                Double val = values[month - 1];
                if (val == null) {
                    c.setCellValue(0);
                } else {
                    c.setCellValue(val);
                }
            }
        }

        // --- Totals ---
        int totalStartRow = pivotStartRow + 15;
        Row totalHeader = sheet.createRow(totalStartRow);
        totalHeader.createCell(0).setCellValue("Status");
        totalHeader.createCell(1).setCellValue("Total");

        int totalRow = totalStartRow + 1;
        for (Map.Entry<String, Double[]> e : statusSeries.entrySet()) {
            double total = Arrays.stream(e.getValue())
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .sum();
            Row r = sheet.createRow(totalRow++);
            r.createCell(0).setCellValue(e.getKey());
            r.createCell(1).setCellValue(total);
        }

        // === Chart code ===

        XDDFDataSource<String> months = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(pivotStartRow + 1, pivotStartRow + 12, 0, 0));

        // Layout config
        int chartHeight = 15;
        int chartWidth = 8;
        int vGap = 2;
        int hGap = 2;
        int chartStartRow = startRow + 1;
        int chartStartCol = 10;

        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // === Bar chart ===
        XSSFChart barChart = drawing.createChart(
                createAnchor(0, 0, chartStartRow, chartStartCol, chartHeight, chartWidth, vGap, hGap));
        barChart.setTitleText(blockTitle + " - Bar Chart");
        XDDFCategoryAxis catBar = barChart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valBar = barChart.createValueAxis(AxisPosition.LEFT);
        valBar.setMinimum(0.0);
        XDDFChartData barData = barChart.createData(ChartTypes.BAR, catBar, valBar);
        ((XDDFBarChartData) barData).setBarDirection(BarDirection.COL);

        int statusCol = 1;
        for (String status : statusSeries.keySet()) {
            XDDFNumericalDataSource<Double> vals = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(pivotStartRow + 1, pivotStartRow + 12, statusCol, statusCol));
            barData.addSeries(months, vals).setTitle(status, null);
            statusCol++;
        }
        barChart.plot(barData);
        barChart.getOrAddLegend().setPosition(LegendPosition.RIGHT);

        // === Line chart ===
        XSSFChart lineChart = drawing.createChart(
                createAnchor(0, 1, chartStartRow, chartStartCol, chartHeight, chartWidth, vGap, hGap));
        lineChart.setTitleText(blockTitle + " - Line Chart");
        XDDFCategoryAxis catLine = lineChart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis valLine = lineChart.createValueAxis(AxisPosition.LEFT);
        valLine.setMinimum(0.0);
        valLine.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFChartData lineData = lineChart.createData(ChartTypes.LINE, catLine, valLine);
        statusCol = 1;
        for (String status : statusSeries.keySet()) {
            XDDFNumericalDataSource<Double> vals = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                    new CellRangeAddress(pivotStartRow + 1, pivotStartRow + 12, statusCol, statusCol));
            XDDFLineChartData.Series series = (XDDFLineChartData.Series) lineData.addSeries(months, vals);
            series.setTitle(status, null);
            series.setSmooth(true);
            series.setMarkerStyle(MarkerStyle.CIRCLE);
            statusCol++;
        }
        lineChart.plot(lineData);
        lineChart.getOrAddLegend().setPosition(LegendPosition.RIGHT);

        // === Pie chart ===
        XDDFDataSource<String> pieCats = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(totalStartRow + 1, totalRow - 1, 0, 0));
        XDDFNumericalDataSource<Double> pieVals = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(totalStartRow + 1, totalRow - 1, 1, 1));

        XSSFChart pieChart = drawing.createChart(
                createAnchor(1, 0, chartStartRow, chartStartCol, chartHeight, chartWidth, vGap, hGap));
        pieChart.setTitleText(blockTitle + " - Pie Chart");
        XDDFChartData pieData = pieChart.createData(ChartTypes.PIE, null, null);
        pieData.addSeries(pieCats, pieVals);
        pieChart.plot(pieData);
        pieChart.getOrAddLegend().setPosition(LegendPosition.RIGHT);

        // === Doughnut chart ===
        XSSFChart doughChart = drawing.createChart(
                createAnchor(1, 1, chartStartRow, chartStartCol, chartHeight, chartWidth, vGap, hGap));
        doughChart.setTitleText(blockTitle + " - Doughnut Chart");
        doughChart.setTitleOverlay(false);
        doughChart.getOrAddLegend().setPosition(LegendPosition.RIGHT);
        XDDFDoughnutChartData doughData = (XDDFDoughnutChartData) doughChart.createData(ChartTypes.DOUGHNUT, null, null);
        doughData.setVaryColors(true);
        doughData.setHoleSize(50);
        doughData.addSeries(pieCats, pieVals);
        doughChart.plot(doughData);

        // --- Auto size ---
        int lastColIndex = statusSeries.size();
        for (int i = 0; i <= lastColIndex; i++) {
            sheet.autoSizeColumn(i);
        }
        return totalRow + 15;
    }

    private XSSFClientAnchor createAnchor(int chartRow, int chartCol,
                                          int startRow, int startCol,
                                          int chartHeight, int chartWidth,
                                          int vGap, int hGap) {
        int col1 = startCol + chartCol * (chartWidth + hGap);
        int col2 = col1 + chartWidth;
        int row1 = startRow + chartRow * (chartHeight + vGap);
        int row2 = row1 + chartHeight;
        return new XSSFClientAnchor(0, 0, 0, 0, col1, row1, col2, row2);
    }
}
