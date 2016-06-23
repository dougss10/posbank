package br.com.rp.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import br.com.rp.domain.Agencia;
import br.com.rp.domain.Cliente;
import br.com.rp.domain.Conta;
import br.com.rp.domain.Proposta;
import br.com.rp.domain.SituacaoCliente;
import br.com.rp.domain.SituacaoProposta;
import br.com.rp.domain.TipoConta;
import br.com.rp.repository.AgenciaRepository;
import br.com.rp.repository.ClienteRepository;
import br.com.rp.repository.ContaRepository;
import br.com.rp.repository.PropostaRepository;
import br.com.rp.services.EmailService;
import br.com.rp.services.PropostaService;
import br.com.rp.services.exception.ClienteComPropostaComMenosDe30DiasException;
import br.com.rp.services.exception.ClienteJaAtivoTentandoRegistrarUmaNovaPropostaException;

@Stateless
public class PropostaServiceImpl implements PropostaService {
	
	private static final String PROPOSTA_ACEITA = "proposta aceita! :D";

	@EJB
	private PropostaRepository propostaRepository;
	
	@EJB
	EmailService emailService;
	
	@EJB
	ClienteRepository clienteRepository;
	
	@EJB
	ContaRepository contaRepository;
	
	@EJB
	AgenciaRepository agenciaRepository;
	
	@Override
	public Proposta processoParaRegistrarUmaProposta(Proposta proposta) {
		oCPFDoClienteJaExisteEJaTemPropostaAceita(proposta.getCliente());
		oClienteTemPropostaComMenosDe30Dias(proposta.getCliente());
		registrarProposta(proposta);
		return proposta;
	}
	
	@Override	
	public void oClienteTemPropostaComMenosDe30Dias(Cliente cliente) {		
		if(cliente.getId() != null) {
			List<Proposta> propostas = this.propostaRepository.procurarPorPropostasComMenosDe30DiasDoCliente(cliente);
			if(propostas.size() > 0)
				throw new ClienteComPropostaComMenosDe30DiasException("Você só pode enviar uma nova proposta, apos 30 dias da prospota que voce já enviou.");
		}
	}

	@Override
	public void oCPFDoClienteJaExisteEJaTemPropostaAceita(Cliente cliente) {
		if(cliente.getId() != null) {
			if(this.propostaRepository.verificarSeOClienteJaEstaAtivo(cliente))
				throw new ClienteJaAtivoTentandoRegistrarUmaNovaPropostaException("Você já um cliente do nosso banco, você não pode enviar novas proposta.");
		}
	}

	@Override
	public Proposta registrarProposta(Proposta proposta) {
		return this.propostaRepository.save(proposta);
	}
	
	public List<Proposta> pesquisarPropostasPorEstado(String estado) {

		List<Proposta> propostas = propostaRepository.procurarProspostasPorEstado(estado);

		return propostas;
	}

	@Override
	public boolean aceitarProposta(Long propostaId, Long agenciaId, TipoConta tipoConta, BigDecimal limiteDaConta, String textoEmail) {
		
		Proposta proposta = propostaRepository.findById(propostaId);
		proposta.setSituacao(SituacaoProposta.AC);

		propostaRepository.save(proposta);	
		
		Cliente cliente = clienteRepository.findById(proposta.getCliente().getId());
		
		cliente.setSituacao(SituacaoCliente.ATIVO);
		
		clienteRepository.save(cliente);
		
		Agencia agencia = agenciaRepository.findById(agenciaId);
		
		Conta conta = new Conta();
		conta.setAgencia(agencia);
		conta.setCliente(cliente);
		conta.setLimite(limiteDaConta);
		conta.setNumero(100000 + new Random().nextInt() * 900000);
		conta.setTipoConta(tipoConta);	
		
		try {
			emailService.enviarEmail(cliente.getEmail().toString(), PROPOSTA_ACEITA, textoEmail);
		} catch (MessagingException e) {
			//TODO criar uma error personalizado para erro d eenvio de e-mail.
			e.printStackTrace();
		}
		return true;
	}
	
	
	

	
}
//Preciso implementar o teste do methodo procurarProspostasPorEstado da class PropostaRepositoryImpl


//pesquisarporposta por estado que estão com situações recebidas usar a enum
//acietar proposta(ID da proposta)  ===>  mudar o status de cleinte para ativo ===>  criar conta corrente numero da conta random inserir o cliente ativado====> enviar email
//rejeitar proposta(ID da proposta, string motivo rejeição) ===> informar o motivo da rejeição ===> enviar e-mail
//enviar e-mail