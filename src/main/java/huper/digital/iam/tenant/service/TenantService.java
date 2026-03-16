package huper.digital.iam.tenant.service;

import huper.digital.iam.tenant.dto.TenantDTO;
import huper.digital.iam.tenant.dto.TenantUpsertRequest;
import huper.digital.iam.tenant.entity.TenantEntity;
import huper.digital.iam.tenant.repository.TenantRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class TenantService {

  @Inject
  TenantRepository tenantRepository;

  public List<TenantDTO> findAll() {
    return tenantRepository.listAll().stream().map(this::toDto).toList();
  }

  public TenantDTO findById(Long id) {
    return tenantRepository.findByIdOptional(id)
        .map(this::toDto)
        .orElseThrow(() -> new NotFoundException("Tenant não encontrado: " + id));
  }

  @Transactional
  public TenantDTO create(TenantUpsertRequest request) {
    tenantRepository.findByName(request.name()).ifPresent(o -> {
      throw new IllegalArgumentException("Já existe um tenant com o nome: " + request.name());
    });
    TenantEntity entity = new TenantEntity();
    entity.setName(request.name());
    entity.setTaxIdentifier(request.taxIdentifier());
    entity.setAddress(request.address());
    entity.setEmail(request.email());
    entity.setPhone(request.phone());
    entity.setFoundingDate(request.foundingDate());
    entity.setIsActive(request.active() == null ? Boolean.TRUE : request.active());
    tenantRepository.persist(entity);
    return toDto(entity);
  }

  @Transactional
  public TenantDTO update(Long id, TenantUpsertRequest request) {
    TenantEntity entity = tenantRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Tenant não encontrado: " + id));
    if (request.name() != null && !request.name().equalsIgnoreCase(entity.getName())) {
      tenantRepository.findByName(request.name()).ifPresent(o -> {
        throw new IllegalArgumentException("Já existe um tenant com o nome: " + request.name());
      });
      entity.setName(request.name());
    }
    if (request.taxIdentifier() != null) entity.setTaxIdentifier(request.taxIdentifier());
    if (request.address() != null) entity.setAddress(request.address());
    if (request.email() != null) entity.setEmail(request.email());
    if (request.phone() != null) entity.setPhone(request.phone());
    if (request.foundingDate() != null) entity.setFoundingDate(request.foundingDate());
    if (request.active() != null) entity.setIsActive(request.active());
    tenantRepository.persist(entity);
    return toDto(entity);
  }

  @Transactional
  public void delete(Long id) {
    TenantEntity entity = tenantRepository.findByIdOptional(id)
        .orElseThrow(() -> new NotFoundException("Tenant não encontrado: " + id));
    if (Boolean.TRUE.equals(entity.getIsDefault())) {
      throw new IllegalStateException("Não é possível excluir o tenant padrão.");
    }
    tenantRepository.delete(entity);
  }

  private TenantDTO toDto(TenantEntity e) {
    return new TenantDTO(
        e.getId(), e.getName(), e.getTaxIdentifier(), e.getAddress(),
        e.getEmail(), e.getPhone(), e.getFoundingDate(), e.getIsActive(), e.getIsDefault()
    );
  }
}
