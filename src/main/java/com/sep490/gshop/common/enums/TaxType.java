package com.sep490.gshop.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sep490.gshop.config.handler.AppException;

public enum TaxType {
    VAT, MFN, RCEPT, VJEPA, ACFTA, AJCEP, VKFTA, AKFTA, UKVFTA, TTDB,NHAP_KHAU_UU_DAI, NHAP_KHAU_TOI_UU, CPTPP;
}
