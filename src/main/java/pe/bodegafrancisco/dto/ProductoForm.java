package pe.bodegafrancisco.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import pe.bodegafrancisco.model.Producto;

/** Datos del formulario de producto. */
public class ProductoForm {
    @NotBlank(message = "Ingresa el nombre del producto")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombre;
    @NotNull(message = "Selecciona una categoría")
    private Integer categoriaId;
    @NotNull(message = "Ingresa el precio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    @DecimalMax(value = "9999.99", message = "El precio máximo es 9999.99")
    private BigDecimal precio;
    @NotNull(message = "Ingresa el stock inicial")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 100000, message = "Stock máximo: 100000")
    private Integer stock = 0;

    public static ProductoForm desde(Producto p) {
        var f = new ProductoForm();
        f.nombre = p.nombre();
        f.categoriaId = p.categoriaId();
        f.precio = p.precio();
        f.stock = p.stock();
        return f;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String v) {
        this.nombre = v;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer v) {
        this.categoriaId = v;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal v) {
        this.precio = v;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer v) {
        this.stock = v;
    }
}