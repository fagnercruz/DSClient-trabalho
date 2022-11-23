package br.estudos.devsuperior.dsclient.service;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.estudos.devsuperior.dsclient.dto.ClientDTO;
import br.estudos.devsuperior.dsclient.entities.Client;
import br.estudos.devsuperior.dsclient.repository.ClientRepository;
import br.estudos.devsuperior.dsclient.service.exceptions.DatabaseException;
import br.estudos.devsuperior.dsclient.service.exceptions.ResourceNotFoundException;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;
	
	@Transactional(readOnly = true)
	public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
		Page<Client> list = repository.findAll(pageRequest);
		return list.map(item -> new ClientDTO(item));
	}
	
	@Transactional(readOnly = true)
	public ClientDTO findById(Long id) {
		Optional<Client> obj = repository.findById(id);
		Client entity = obj.orElseThrow( () -> new ResourceNotFoundException("Cliente não encontrado (Client not found)"));  
		return new ClientDTO(entity);
	}
	
	@Transactional
	public ClientDTO save(ClientDTO dto) {
		Client entity = new Client();
		copyDTOToEntity(dto, entity);
		entity = repository.save(entity);
		return new ClientDTO(entity); 
	}
	
	@Transactional
	public ClientDTO update(Long id, ClientDTO dto) {
		
		// Não usar o findById pq aí a operação de update vai usar 2 requisições ao banco
		// será usado uma nova abordagem para obter o objeto sem consulta ao banco
		
		try {
			Client entity =  repository.getReferenceById(id);
			copyDTOToEntity(dto, entity);
			entity = repository.save(entity);
			return new ClientDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		}
		
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade (Integrity voilation)");
		}
		
	}
	
	private void copyDTOToEntity(ClientDTO dto, Client entity) {
		entity.setName(dto.getName());
		entity.setCpf(dto.getCpf());
		entity.setIncome(dto.getIncome());
		entity.setBirthDate(dto.getBirthDate());
		entity.setChildren(dto.getChildren());
	}
}
