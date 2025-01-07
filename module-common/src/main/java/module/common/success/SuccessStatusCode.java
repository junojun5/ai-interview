package module.common.success;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessStatusCode {
    /**
     * success code
     */
    OK(200),
    CREATED(201);

    private final int status;
}
