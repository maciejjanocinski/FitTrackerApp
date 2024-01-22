package app.bodymetrics;

import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
 class BodyMetricsService {
    private final UserService userService;
    private final BodyMetricsMapper bodyMetricsMapper = BodyMetricsMapper.INSTANCE;

    public BodyMetricsDto getBodyMetrics(Authentication authentication) {
        BodyMetrics bodyMetrics = userService.getUserByUsername(authentication.getName()).getBodyMetrics();
        return bodyMetricsMapper.mapToDto(bodyMetrics);
    }

    @Transactional
    public BodyMetricsDto updateBodyMetrics(Authentication authentication, UpdateBodyMetricsDto updateBodyMetricsDto) {
        BodyMetrics bodyMetrics = userService.getUserByUsername(authentication.getName()).getBodyMetrics();
        bodyMetrics.updateBodyMetrics(updateBodyMetricsDto);

        return bodyMetricsMapper.mapToDto(bodyMetrics);
    }
}
