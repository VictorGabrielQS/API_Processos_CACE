package tech.buildrun.api.model;

import java.time.LocalDate;


public class Processo {


    public  Long id;
    private String numero;
    private String numeroCurto;
    private LocalDate dataEntradaProjudi;
    private LocalDate dataEntradaSapjud;
    private LocalDate ultimaAlteracao;
    private String consultor;
    private int prazo;
    private String situacao;
    private String descricao;



    //Id

    public Long getId(){
        return  id;
    }


    //Numero :

    public void setNumero(String numero){
        this.numero = numero;

    }

    public String getNumero(){
        return numero;
    }



    //Numero Curto :

    public void  setNumeroCurto(String numeroCurto){
        this.numeroCurto = numeroCurto;
    }

    public String getNumeroCurto(){
        return numeroCurto;
    }




    //Data Entrada Projudi

    public LocalDate getDataEntradaProjudi() {
        return dataEntradaProjudi;
    }

    public void setDataEntradaProjudi(LocalDate dataEntradaProjudi) {
        this.dataEntradaProjudi = dataEntradaProjudi;
    }




    //Data Entrada SapJud

    public LocalDate getDataEntradaSapjud() {
        return dataEntradaSapjud;
    }

    public void setDataEntradaSapjud(LocalDate dataEntradaSapjud) {
        this.dataEntradaSapjud = dataEntradaSapjud;
    }


    //Data da ultima alteracao
    public  LocalDate getUltimaAlteracao(){
        return ultimaAlteracao;
    }

    public  void setUltimaAlteracao (LocalDate ultimaAlteracao){
            this.ultimaAlteracao = ultimaAlteracao;
    }




    //Consultor

    public String getConsultor() {
        return consultor;
    }

    public void setConsultor(String consultor) {
        this.consultor = consultor;
    }




    //Prazo

    public int getPrazo() {
        return prazo;
    }

    public void setPrazo(int prazo) {
        this.prazo = prazo;
    }




    //Situacao

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }




    //Descricao

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }




}
