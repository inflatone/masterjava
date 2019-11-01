package ru.javaops.masterjava.web;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.javaops.masterjava.ExceptionType;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class FaultInfo {
    @NonNull
    private ExceptionType type;

    @Override
    public String toString() {
        return type.toString();
    }
}
