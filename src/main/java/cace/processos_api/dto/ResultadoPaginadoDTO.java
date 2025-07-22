package cace.processos_api.dto;

import java.io.Serializable;

public class ResultadoPaginadoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object dados;
    private int quantidadeRestante;

    public ResultadoPaginadoDTO(Object dados, int quantidadeRestante) {
        this.dados = dados;
        this.quantidadeRestante = quantidadeRestante;
    }

    public Object getDados() {
        return dados;
    }

    public int getQuantidadeRestante() {
        return quantidadeRestante;
    }

    public void setDados(Object dados) {
        this.dados = dados;
    }

    public void setQuantidadeRestante(int quantidadeRestante) {
        this.quantidadeRestante = quantidadeRestante;
    }
}
