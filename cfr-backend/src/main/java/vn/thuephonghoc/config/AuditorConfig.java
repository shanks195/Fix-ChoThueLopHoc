package vn.thuephonghoc.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import vn.thuephonghoc.utils.SecurityUtils;

import java.util.Optional;

/**
 * @Description: Set audit
 */
@Component("auditorAware")
public class AuditorConfig implements AuditorAware<String> {

    /**
     * Return operator logo information
     *
     * @return
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // Here you should get specific information based on actual business conditions
        return Optional.of(SecurityUtils.getCurrentUsername());
    }
}
