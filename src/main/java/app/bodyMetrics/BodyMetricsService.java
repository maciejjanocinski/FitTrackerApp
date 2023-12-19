package app.bodyMetrics;

import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static app.bodyMetrics.BodyMetricsMapper.mapBodyMetricsToBodyMetricsDto;
import static app.bodyMetrics.BodyMetricsMapper.updateBodyMetrics;

@Service
@RequiredArgsConstructor
public class BodyMetricsService {
    private final UserService userService;

    public BodyMetricsDto getBodyMetrics(Authentication authentication) {
        BodyMetrics bodyMetrics = userService.getUserByUsername(authentication.getName()).getBodyMetrics();
        return mapBodyMetricsToBodyMetricsDto(bodyMetrics);
    }

    @Transactional
    public BodyMetricsDto setBodyMetrics(Authentication authentication, AddBodyMetricsDto addBodyMetricsDto) {
        BodyMetrics bodyMetrics = userService.getUserByUsername(authentication.getName()).getBodyMetrics();
        updateBodyMetrics(bodyMetrics, addBodyMetricsDto);
        return mapBodyMetricsToBodyMetricsDto(bodyMetrics);
    }
}
