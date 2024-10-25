package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistingDataException;

import java.util.HashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private static long nextId = 1;

    public static Long generateId() {
        return nextId++;
    }

    public User create(User user) {
        long id = generateId();
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    public User get(long id) {
        return users.get(id);
    }

    public User update(long id, User user) {
        User oldUser = users.get(id);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (emailCheck(user.getEmail())) {
                throw new ExistingDataException("Этот имейл уже занят.");
            } else {
                oldUser.setEmail(user.getEmail());
            }
        }
        users.put(id, oldUser);
        return users.get(id);
    }

    public void delete(long id) {
        users.remove(id);
    }

    public boolean emailCheck(String email) {
        if (email == null) {
            return false;
        }
        return !users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toSet())
                .isEmpty();
    }

}
