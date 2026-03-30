package com.uranny.yourhousehelper.external.openai.service;

import com.uranny.yourhousehelper.external.openai.dto.request.ReportAiRequestDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {

    private final ChatClient chatClient;

    public OpenAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getReportResponse(ReportAiRequestDto req) {

        String prompt = """
                당신은 전문 데이터 분석가이자 미래 컨설팅 전문가 AI입니다.
                다음 사용자의 소비 데이터를 분석해 Markdown 보고서를 작성하세요.
                
                # 사용자 정보
                ## 저축 이유
                %s
                
                ## 목표 금액
                %d
                
                # 이번 달 분석
                
                ## 수입
                항목: %s  
                금액: %d
                
                ## 지출
                항목: %s  
                금액: %d
                
                ## 총합
                %d
                
                ## 가장 많이 지출한 항목
                %s
                
                # 지난 달 분석
                
                ## 수입
                항목: %s  
                금액: %d
                
                ## 지출
                항목: %s  
                금액: %d
                
                ## 총합
                %d
                """.formatted(
                req.getReason(),
                req.getFinalMoney(),
                req.getKeyIncomes(),
                req.getTotalIncomeMoney(),
                req.getKeyExpenses(),
                req.getTotalExpenseMoney(),
                req.getTotalMoney(),
                req.getTopExpenses(),
                req.getPastKeyIncomes(),
                req.getPastTotalIncomeMoney(),
                req.getPastKeyExpenses(),
                req.getPastTotalExpenseMoney(),
                req.getPastTotalMoney()
        );

        return chatClient.prompt()
                .user(prompt)
                .options(OpenAiChatOptions.builder().model(OpenAiApi.ChatModel.GPT_4_O_MINI).build())
                .call()
                .content();
    }
}