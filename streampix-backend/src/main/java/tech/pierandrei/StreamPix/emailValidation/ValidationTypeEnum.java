package tech.pierandrei.StreamPix.emailValidation;

public enum ValidationTypeEnum {
        ACCOUNT_ACTIVATION,
        PASSWORD_RECOVERY,
        EMAIL_CHANGE;

        public static ValidationTypeEnum fromString(String value) {
        for (ValidationTypeEnum type : ValidationTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo inválido: " + value);
    }
}

