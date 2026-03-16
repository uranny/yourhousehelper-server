package com.uranny.yourhousehelper.external.openai.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportAiRequestDto {
    // user
    private String reason;
    private Long finalMoney;

    // current month
    private Long totalMoney;
    private Long totalExpenseMoney;
    private Long totalIncomeMoney;
    private String keyExpenses;
    private String keyIncomes;
    private String topExpenses;

    // past month
    private Long pastTotalMoney;
    private Long pastTotalExpenseMoney;
    private Long pastTotalIncomeMoney;
    private String pastKeyExpenses;
    private String pastKeyIncomes;
}
