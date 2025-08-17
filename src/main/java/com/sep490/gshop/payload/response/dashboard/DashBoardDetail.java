package com.sep490.gshop.payload.response.dashboard;

import com.sep490.gshop.payload.response.subclass.PRStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashBoardDetail {
    private String dashBoardName;
    private long total;
    private List<PRStatus> statusList;
}
