package com.bamdoliro.maru.infrastructure.neis.feign.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NeisSchoolResponse {

    public List<SchoolInfo> schoolInfo;

    @Getter
    @NoArgsConstructor
    public static class SchoolInfo {

        public List<Head> head;
        public List<Row> row;

        @Getter
        public static class Head {

            @JsonProperty("list_total_count")
            public int listTotalCount;

            @JsonProperty("RESULT")
            public RESULT result;

            @Getter
            public static class RESULT {

                @JsonProperty("CODE")
                public String code;

                @JsonProperty("MESSAGE")
                public String message;
            }
        }

        @Getter
        public static class Row {

            @JsonProperty("ATPT_OFCDC_SC_CODE")
            public String officeCode;

            @JsonProperty("ATPT_OFCDC_SC_NM")
            public String officeName;

            @JsonProperty("SD_SCHUL_CODE")
            public String standardSchoolCode;

            @JsonProperty("SCHUL_NM")
            public String schoolName;

            @JsonProperty("ENG_SCHUL_NM")
            public String englishSchoolName;

            @JsonProperty("SCHUL_KND_SC_NM")
            public String kindOfSchool;

            @JsonProperty("LCTN_SC_NM")
            public String location;

            @JsonProperty("JU_ORG_NM")
            public String origin;

            @JsonProperty("FOND_SC_NM")
            public String foundation;

            @JsonProperty("ORG_RDNZC")
            public String zoneCode;

            @JsonProperty("ORG_RDNMA")
            public String address;

            @JsonProperty("ORG_RDNDA")
            public String detailAddress;

            @JsonProperty("ORG_TELNO")
            public String tellNumber;

            @JsonProperty("HMPG_ADRES")
            public String homePageUrl;

            @JsonProperty("COEDU_SC_NM")
            public String genderType;

            @JsonProperty("ORG_FAXNO")
            public String faxNumber;

            @JsonProperty("HS_SC_NM")
            public String highSchoolType;

            @JsonProperty("INDST_SPECL_CCCCL_EXST_YN")
            public String isIndustrySpecialClassExist;

            @JsonProperty("HS_GNRL_BUSNS_SC_NM")
            public String highSchoolGeneralOrBusiness;

            @JsonProperty("SPCLY_PURPS_HS_ORD_NM")
            public String specialPurposeHighSchoolOrder;

            @JsonProperty("ENE_BFE_SEHF_SC_NM")
            public String admissionTimeType;

            @JsonProperty("DGHT_SC_NM")
            public String dayAndNightType;

            @JsonProperty("FOND_YMD")
            public String foundationDate;

            @JsonProperty("FOAS_MEMRD")
            public String schoolAnniversaryDate;

            @JsonProperty("LOAD_DTM")
            public String loadDateTime;
        }

    }

    public List<SchoolInfo.Row> getSchoolInfo() {
        if (this.schoolInfo == null) {
            return List.of();
        }

        return this.schoolInfo.get(1).getRow();
    }
}
