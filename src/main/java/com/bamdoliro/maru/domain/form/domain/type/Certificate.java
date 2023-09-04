package com.bamdoliro.maru.domain.form.domain.type;

import com.bamdoliro.maru.shared.property.EnumProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Certificate implements EnumProperty {

    CRAFTSMAN_INFORMATION_PROCESSING("정보처리기능사", 4),
    CRAFTSMAN_INFORMATION_EQUIPMENT_OPERATION("정보기기운용기능사", 4),
    CRAFTSMAN_COMPUTER("전자계산기기능사", 4),
    COMPUTER_SPECIALIST_LEVEL_1("컴퓨터활용능력1급", 3),
    COMPUTER_SPECIALIST_LEVEL_2("컴퓨터활용능력2급", 2),
    COMPUTER_SPECIALIST_LEVEL_3("컴퓨터활용능력3급", 1),
    ;

    private final String description;
    private final int score;

    public boolean isComputerSpecialist() {
        return this == COMPUTER_SPECIALIST_LEVEL_1
                || this == COMPUTER_SPECIALIST_LEVEL_2
                || this == COMPUTER_SPECIALIST_LEVEL_3;
    }


    @Override
    public String toString() {
        return this.getDescription() + "(" + this.getScore() + "점)";
    }
}
