package com.bamdoliro.maru.infrastructure.xlsx;

import com.bamdoliro.maru.domain.form.domain.Form;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public class FormColumn {

    private final Function<Form, Object> extractor;
    private final String style;

    public static FormColumn text(Function<Form, Object> extractor) {
        return new FormColumn(extractor, "default");
    }

    public static FormColumn score(Function<Form, Object> extractor) {
        return new FormColumn(extractor, "right");
    }

}
