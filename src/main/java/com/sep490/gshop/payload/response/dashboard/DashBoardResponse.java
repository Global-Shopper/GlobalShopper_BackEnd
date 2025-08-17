package com.sep490.gshop.payload.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashBoardResponse {
    List<DashBoardDetail> dashBoardList;

    public void addDashBoardDetail(DashBoardDetail dashBoardDetail) {
        if (this.dashBoardList == null) {
            this.dashBoardList = new java.util.ArrayList<>();
        }
        this.dashBoardList.add(dashBoardDetail);
    }
}
