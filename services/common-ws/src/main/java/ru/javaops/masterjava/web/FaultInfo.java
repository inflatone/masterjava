package ru.javaops.masterjava.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.javaops.masterjava.ExceptionType;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@XmlType(namespace = "http://common.javaops.ru/")
public class FaultInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private ExceptionType type;

    @Override
    public String toString() {
        return type.toString();
    }
}
