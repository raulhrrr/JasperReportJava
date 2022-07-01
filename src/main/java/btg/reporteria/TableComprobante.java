package btg.reporteria;

public class TableComprobante {

    private String cocDescripcion;
    private String cocValor;
    private String cocIva;
    private String cocTotal;

    public TableComprobante(String cocDescripcion, String cocValor, String cocIva, String cocTotal) {
        this.cocDescripcion = cocDescripcion;
        this.cocValor = cocValor;
        this.cocIva = cocIva;
        this.cocTotal = cocTotal;
    }

    public String getCocDescripcion() {
        return cocDescripcion;
    }

    public void setCocDescripcion(String cocDescripcion) {
        this.cocDescripcion = cocDescripcion;
    }

    public String getCocValor() {
        return cocValor;
    }

    public void setCocValor(String cocValor) {
        this.cocValor = cocValor;
    }

    public String getCocIva() {
        return cocIva;
    }

    public void setCocIva(String cocIva) {
        this.cocIva = cocIva;
    }

    public String getCocTotal() {
        return cocTotal;
    }

    public void setCocTotal(String cocTotal) {
        this.cocTotal = cocTotal;
    }

}
