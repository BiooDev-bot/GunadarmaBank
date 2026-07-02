package mbanking.enums;

public enum TransactionType {
    DEPOSIT("Setor Tunai"),
    WITHDRAW("Tarik Tunai"),
    TRANSFER_OUT("Transfer Keluar"),
    TRANSFER_IN("Transfer Masuk");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
