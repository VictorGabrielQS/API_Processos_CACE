package tech.buildrun.api.model;

import java.time.LocalDate;

public class Processo {

    public  Long id;
    private String numero;
    private String numeroCurto;
    private LocalDate dataEntradaProjudi;
    private LocalDate dataEntradaSapjud;
    private String consultor;
    private int prazo;
    private Situacao situacao;
    private String descricao;

    public enum Situacao {
        ABERTO , ENCERRADO , EM_ANDAMENTO
    }


    //numero :

    public void setNumero(String numero){
        this.numero = numero;

    }

    public String getNumero(){
        return numero;
    }






}
