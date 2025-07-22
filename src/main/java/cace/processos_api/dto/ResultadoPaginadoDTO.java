package cace.processos_api.dto;

public class ResultadoPaginadoDTO {
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
}
