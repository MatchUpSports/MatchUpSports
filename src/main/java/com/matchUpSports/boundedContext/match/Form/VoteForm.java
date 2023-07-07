package com.matchUpSports.boundedContext.match.Form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class VoteForm {
    private long toVote;        // 누구에게 투표했는 지
    private int voteTypeCode;   // 어떤 사유로 투표했는 지
}
