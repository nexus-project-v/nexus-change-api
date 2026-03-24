package br.com.nexus.change.core.service;

import br.com.nexus.change.commons.exception.ResourceFoundException;
import br.com.nexus.change.core.domain.component.ChangeComponent;
import br.com.nexus.change.core.ports.in.component.*;
import br.com.nexus.change.core.ports.out.ComponentRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ComponentService implements CreateComponentPort, UpdateComponentPort, FindByIdComponentsPort, FindComponentPort, DeleteComponentPort {

    private final ComponentRepositoryPort componentRepositoryPort;

    @Autowired
    public ComponentService(ComponentRepositoryPort componentRepositoryPort) {
        this.componentRepositoryPort = componentRepositoryPort;
    }

    @Override
    public ChangeComponent save(ChangeComponent component) {
        log.info("operation=component.save stage=start traceId={} componentId={} componentName={} componentVersion={}",
                traceId(),
                componentId(component),
                componentName(component),
                componentVersion(component));

        ChangeComponent saved = componentRepositoryPort.save(component);
        if (saved != null) {
            log.info("operation=component.save stage=success traceId={} componentId={} componentName={} componentVersion={}",
                    traceId(),
                    saved.getId(),
                    saved.getName(),
                    saved.getVersion());
        } else {
            log.warn("operation=component.save stage=finish traceId={} result=null componentName={}",
                    traceId(),
                    componentName(component));
        }

        return saved;
    }

    @Override
    public ChangeComponent update(UUID id, ChangeComponent component) {
        log.info("operation=component.update stage=start traceId={} componentId={} componentName={} componentVersion={}",
                traceId(),
                id,
                componentName(component),
                componentVersion(component));

        ChangeComponent componentById = findById(id);
        if (componentById != null) {
            componentById.update(id, component);

            ChangeComponent updated = componentRepositoryPort.save(componentById);
            if (updated != null) {
                log.info("operation=component.update stage=success traceId={} componentId={} componentName={} componentVersion={}",
                        traceId(),
                        updated.getId(),
                        updated.getName(),
                        updated.getVersion());
            } else {
                log.warn("operation=component.update stage=finish traceId={} componentId={} result=null",
                        traceId(),
                        id);
            }

            return updated;
        }

        log.warn("operation=component.update stage=not_found traceId={} componentId={}", traceId(), id);

        return null;
    }

    @Override
    public ChangeComponent findById(UUID id) {
        log.debug("operation=component.find-by-id stage=start traceId={} componentId={}", traceId(), id);

        ChangeComponent component = componentRepositoryPort.findById(id);
        if (component != null) {
            log.debug("operation=component.find-by-id stage=success traceId={} componentId={} componentName={} componentVersion={}",
                    traceId(),
                    component.getId(),
                    component.getName(),
                    component.getVersion());
        } else {
            log.warn("operation=component.find-by-id stage=not_found traceId={} componentId={}", traceId(), id);
        }

        return component;
    }

    @Override
    public List<ChangeComponent> findAll() {
        try {
            log.debug("operation=component.find-all stage=start traceId={}", traceId());

            List<ChangeComponent> components = componentRepositoryPort.findAll();
            int resultCount = components != null ? components.size() : 0;

            log.debug("operation=component.find-all stage=success traceId={} resultCount={}", traceId(), resultCount);
            return components;
        } catch (Exception e) {
            log.error("operation=component.find-all stage=error traceId={} message={}", traceId(), e.getMessage(), e);
        }

        return null;
    }

    @Override
    public boolean remove(UUID id) {
        try {
            log.info("operation=component.remove stage=start traceId={} componentId={}", traceId(), id);

            ChangeComponent componentById = findById(id);
            if (componentById == null) {
                log.warn("operation=component.remove stage=not_found traceId={} componentId={}", traceId(), id);
                throw new ResourceFoundException("Component not found");
            }

            componentRepositoryPort.remove(id);
            log.info("operation=component.remove stage=success traceId={} componentId={}", traceId(), id);
            return Boolean.TRUE;
        } catch (ResourceFoundException e) {
            log.error("operation=component.remove stage=error traceId={} componentId={} message={}",
                    traceId(),
                    id,
                    e.getMessage());
            return Boolean.FALSE;
        }
    }

    private UUID componentId(ChangeComponent component) {
        return component != null ? component.getId() : null;
    }

    private String componentName(ChangeComponent component) {
        return component != null ? component.getName() : null;
    }

    private String componentVersion(ChangeComponent component) {
        return component != null ? component.getVersion() : null;
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return traceId == null || traceId.trim().isEmpty() ? "N/A" : traceId;
    }
}
