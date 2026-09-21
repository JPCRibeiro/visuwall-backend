package br.app.visuwall.user.service;

import br.app.visuwall.auth.exception.EmailAlreadyRegisteredException;
import br.app.visuwall.user.domain.User;
import br.app.visuwall.user.exception.UserNotFoundException;
import br.app.visuwall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String email, String rawPassword, String username) {
        String normalizedEmail = email.toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = User.register(
                normalizedEmail, passwordEncoder.encode(rawPassword), username.trim()
        );

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyRegisteredException();
        }
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }
}
