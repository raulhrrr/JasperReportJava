package btg.reporteria;

public class TableFormUno {

    private int numero;
    private String valorUsd;

    public TableFormUno(int numero, String valorUsd) {
        this.numero = numero;
        this.valorUsd = valorUsd;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getValorUsd() {
        return valorUsd;
    }

    public void setValorUsd(String valorUsd) {
        this.valorUsd = valorUsd;
    }

}
