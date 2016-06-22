package br.com.rp.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Assert;
import org.junit.Test;

import br.com.rp.AbstractTest;
import br.com.rp.domain.Agendamento;
import br.com.rp.domain.Pagamento;
import br.com.rp.repository.ContaRepository;
import br.com.rp.services.AgendamentoService;
import br.com.rp.util.Util;

@CleanupUsingScript(phase = TestExecutionPhase.AFTER, value = { "db/agendamento_delete.sql" })
public class AgendamentoServiceTest extends AbstractTest {

	private final Long AGENDAMENTO_TESTE_ID = 1000L;

	@EJB
	private AgendamentoService agendamentoService;

	@EJB
	private ContaRepository contaRepository;
	
	@Test
	@UsingDataSet({ "db/banco.xml", "db/agencia.xml", "db/cliente.xml", "db/conta.xml", "db/pagamento.xml",
			"db/agendamento.xml" })
	public void deveCancelarUmAgendamento() {
		Boolean result = this.agendamentoService.cancelarPagamentoPorID(AGENDAMENTO_TESTE_ID);
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	@UsingDataSet({ "db/banco.xml", "db/agencia.xml", "db/cliente.xml", "db/conta.xml" })
	public void deveRegistrarUmAgendamento() {
		Date data = Util.getDataAtual();
		Pagamento pagamento = new Pagamento();
		pagamento.setValor(new BigDecimal("100"));
		pagamento.setVencimento(data);

		Agendamento agendamento = new Agendamento();
		agendamento.setDescricao("Pagamento");
		agendamento.setDataAgendamento(data);
		agendamento.setPagamento(pagamento);
		agendamento.setConta(this.contaRepository.findById(1000L));

		Boolean result = this.agendamentoService.agendarPagamento(agendamento);
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	@UsingDataSet({ "db/banco.xml", "db/agencia.xml", "db/cliente.xml", "db/conta.xml" })
	public void deveRecusarUmAgendamento() {
		Pagamento pagamento = new Pagamento();
		pagamento.setValor(new BigDecimal("10000.00"));
		pagamento.setVencimento(Util.addData(-2, Util.getDataAtual()));

		Agendamento agendamento = new Agendamento();
		agendamento.setDataAgendamento(Util.getDataAtual());
		agendamento.setPagamento(pagamento);
		agendamento.setConta(this.contaRepository.findById(1000L));

		Boolean result = this.agendamentoService.agendarPagamento(agendamento);
		Assert.assertEquals(Boolean.FALSE, result);
	}

	@Test
	@UsingDataSet({ "db/banco.xml", 
			"db/agencia.xml", 
			"db/cliente.xml", 
			"db/conta.xml", 
			"db/pagamento.xml",
			"db/agendamento.xml" })
	public void devePagarPagamentosAgendados() {
		this.agendamentoService.processarPagamentosAgendadosPara(new Calendar.Builder().setDate(2016, 6, 18).build().getTime());
		List<Agendamento> agendamentos = this.agendamentoService.encontrarAgendamentosPagos();
		Assert.assertEquals(agendamentos.get(0).getPago(), Boolean.TRUE);
	}
}
