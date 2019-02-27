/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Manuel
 */
@Entity
@Table(name = "PROJECTS", catalog = "", schema = "BD")
@NamedQueries({
    @NamedQuery(name = "Projects.findAll", query = "SELECT p FROM Projects p"),
    @NamedQuery(name = "Projects.findByIdp", query = "SELECT p FROM Projects p WHERE p.idp = :idp"),
    @NamedQuery(name = "Projects.findByNome", query = "SELECT p FROM Projects p WHERE p.nome = :nome"),
    @NamedQuery(name = "Projects.findByDono", query = "SELECT p FROM Projects p WHERE p.dono = :dono"),
    @NamedQuery(name = "Projects.findBySaldo", query = "SELECT p FROM Projects p WHERE p.saldo = :saldo"),
    @NamedQuery(name = "Projects.findByPretendido", query = "SELECT p FROM Projects p WHERE p.pretendido = :pretendido"),
    @NamedQuery(name = "Projects.findByInicio", query = "SELECT p FROM Projects p WHERE p.inicio = :inicio"),
    @NamedQuery(name = "Projects.findByFinall", query = "SELECT p FROM Projects p WHERE p.finall = :finall"),
    @NamedQuery(name = "Projects.findByValidade", query = "SELECT p FROM Projects p WHERE p.validade = :validade")})
public class Projects implements Serializable {

    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "IDP")
    private BigDecimal idp;
    @Basic(optional = false)
    @Column(name = "NOME")
    private String nome;
    @Basic(optional = false)
    @Column(name = "DONO")
    private String dono;
    @Basic(optional = false)
    @Column(name = "SALDO")
    private double saldo;
    @Basic(optional = false)
    @Column(name = "PRETENDIDO")
    private double pretendido;
    @Basic(optional = false)
    @Column(name = "INICIO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;
    @Basic(optional = false)
    @Column(name = "FINALL")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finall;
    @Basic(optional = false)
    @Column(name = "VALIDADE")
    private BigInteger validade;

    public Projects() {
    }

    public Projects(BigDecimal idp) {
        this.idp = idp;
    }

    public Projects(BigDecimal idp, String nome, String dono, double saldo, double pretendido, Date inicio, Date finall, BigInteger validade) {
        this.idp = idp;
        this.nome = nome;
        this.dono = dono;
        this.saldo = saldo;
        this.pretendido = pretendido;
        this.inicio = inicio;
        this.finall = finall;
        this.validade = validade;
    }

    public BigDecimal getIdp() {
        return idp;
    }

    public void setIdp(BigDecimal idp) {
        BigDecimal oldIdp = this.idp;
        this.idp = idp;
        changeSupport.firePropertyChange("idp", oldIdp, idp);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        String oldNome = this.nome;
        this.nome = nome;
        changeSupport.firePropertyChange("nome", oldNome, nome);
    }

    public String getDono() {
        return dono;
    }

    public void setDono(String dono) {
        String oldDono = this.dono;
        this.dono = dono;
        changeSupport.firePropertyChange("dono", oldDono, dono);
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        double oldSaldo = this.saldo;
        this.saldo = saldo;
        changeSupport.firePropertyChange("saldo", oldSaldo, saldo);
    }

    public double getPretendido() {
        return pretendido;
    }

    public void setPretendido(double pretendido) {
        double oldPretendido = this.pretendido;
        this.pretendido = pretendido;
        changeSupport.firePropertyChange("pretendido", oldPretendido, pretendido);
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        Date oldInicio = this.inicio;
        this.inicio = inicio;
        changeSupport.firePropertyChange("inicio", oldInicio, inicio);
    }

    public Date getFinall() {
        return finall;
    }

    public void setFinall(Date finall) {
        Date oldFinall = this.finall;
        this.finall = finall;
        changeSupport.firePropertyChange("finall", oldFinall, finall);
    }

    public BigInteger getValidade() {
        return validade;
    }

    public void setValidade(BigInteger validade) {
        BigInteger oldValidade = this.validade;
        this.validade = validade;
        changeSupport.firePropertyChange("validade", oldValidade, validade);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idp != null ? idp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Projects)) {
            return false;
        }
        Projects other = (Projects) object;
        if ((this.idp == null && other.idp != null) || (this.idp != null && !this.idp.equals(other.idp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gui.Projects[ idp=" + idp + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
