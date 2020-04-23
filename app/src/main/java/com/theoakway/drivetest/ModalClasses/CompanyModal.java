package com.theoakway.drivetest.ModalClasses;

public class CompanyModal {

    private String companyName;
    private String companyGuid;

    public CompanyModal(String companyName, String companyGuid) {
        this.companyName = companyName;
        this.companyGuid = companyGuid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyGuid() {
        return companyGuid;
    }

    public void setCompanyGuid(String companyGuid) {
        this.companyGuid = companyGuid;
    }
}
