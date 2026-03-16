package huper.digital.iam.service;

import huper.digital.iam.dto.OrganizationDTO;
import huper.digital.iam.dto.OrganizationUpsertRequest;
import huper.digital.iam.entity.AuthOrganizationEntity;
import huper.digital.iam.repository.AuthOrganizationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class OrganizationService {

  @Inject
  AuthOrganizationRepository organizationRepository;

  public List<OrganizationDTO> findAll() {
    return organizationRepository.listAll().stream().map(this::toDto).toList();
  }

  public OrganizationDTO findById(Long id) {
    return organizationRepository.findByIdOptional(id)
        .map(this::toDto)
        .orElseThrow(() -> new NotFoundException("Organization não encontrada: " + id));
  }

  @Transactional
  public OrganizationDTO create(OrganizationUpsertRequest request) {
    organizationRepository.findByName(request.name()).ifPresent(o -> {
      throw new IllegalArgumentException("Já existe uma organization com o nome: " + request.name());
    });

    AuthOrganizationEntity entity = new AuthOrganizationEntity();
    entity.setName(request.name());
    entity.setTaxIdentifier(request.taxIdentifier());
    entity.setAddress(request.address());
    entity.setEmail(request.email());
    entity.setPhone(request.phone());
    entity.setFoundingDate(request.foundingDate());
    entity.setIsActive(request.active() == null ? Boolean.TRUE : request.active());
    organizationRepository.persist(entity);
    return toDto(entity);
  }

  @Transactional
  public OrganizationDTO update(Long id, OrganizationUpsertRequest request) {
    AuthOrganizationEntity entity = organizationRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Organization não encontrada: " + id));

    if (request.name() != null && !request.name().equalsIgnoreCase(entity.getName())) {
      organizationRepository.findByName(request.name()).ifPresent(o -> {
        throw new IllegalArgumentException("Já existe uma organization com o nome: " + request.name());
      });
      entity.setName(request.name());
    }

    if (request.taxIdentifier() != null) {
      entity.setTaxIdentifier(request.taxIdentifier());
    }
    if (request.address() != null) {
      entity.setAddress(request.address());
    }
    if (request.email() != null) {
      entity.setEmail(request.email());
    }
    if (request.phone() != null) {
      entity.setPhone(request.phone());
    }
    if (request.foundingDate() != null) {
      entity.setFoundingDate(request.foundingDate());
    }
    if (request.active() != null) {
      entity.setIsActive(request.active());
    }

    organizationRepository.persist(entity);
    return toDto(entity);
  }

  @Transactional
  public void delete(Long id) {
    AuthOrganizationEntity entity = organizationRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Organization não encontrada: " + id));
    
    // Prevent deletion of default organization
    if (Boolean.TRUE.equals(entity.getIsDefault())) {
      throw new IllegalStateException("Não é possível excluir a organização padrão.");
    }
    
    organizationRepository.delete(entity);
  }

  private OrganizationDTO toDto(AuthOrganizationEntity entity) {
    return new OrganizationDTO(
        entity.getId(),
        entity.getName(),
        entity.getTaxIdentifier(),
        entity.getAddress(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getFoundingDate(),
        entity.getIsActive(),
        entity.getIsDefault()
    );
  }
}

