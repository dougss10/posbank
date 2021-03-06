package br.com.rp.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.rp.util.Util;

@SuppressWarnings("serial")
@Entity
@Table(name="proposta")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Proposta extends BaseEntity {
	
	@Column(name = "dataCadastro")
	private Timestamp dataCadastro;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;
	
	@Column(name = "rendimento")
	private BigDecimal rendimento;
	
	@Enumerated(EnumType.STRING)	
	private TipoConta tipoConta; 
	
	@Column(name = "mensagem")
	private String mensagem;
	
	@ManyToOne
	@JoinColumn(name = "funcionario_id")
	private Funcionario funcionario;
	
	@Column(name = "dataAvaliacao")
	private Timestamp dataAvaliacao;

	@Column(name="motivoRejeicao", nullable=true)
	private String motivoRejeicao;
	
	@Enumerated(EnumType.STRING)
	private SituacaoProposta situacao;
	
	public Proposta() {
		this.dataCadastro = Util.getDataHoraAtual();
		this.situacao = SituacaoProposta.REC;
	}

	public Timestamp getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Timestamp dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public BigDecimal getRendimento() {
		return rendimento;
	}

	public void setRendimento(BigDecimal rendimento) {
		this.rendimento = rendimento;
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public Timestamp getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(Timestamp dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public String getMotivoRejeicao() {
		return motivoRejeicao;
	}

	public void setMotivoRejeicao(String motivoRejeicao) {
		this.motivoRejeicao = motivoRejeicao;
	}

	public SituacaoProposta getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoProposta situacao) {
		this.situacao = situacao;
	}
}