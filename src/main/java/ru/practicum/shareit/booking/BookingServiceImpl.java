package ru.practicum.shareit.booking;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.UserType;
import ru.practicum.shareit.exception.ExistingDataException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(BookingCreationDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещи с данным id не существует"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования.");
        }
        Booking booking = BookingDtoMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        return BookingDtoMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingApprove(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ExistingDataException("Бронирования с таким id не существует."));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ForbiddenException("Подтверждать или отклонять бронирование может только владелец вещи.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingDtoMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ExistingDataException("Бронирования с таким id не существует."));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new ForbiddenException(
                    "Получение данных о бронировании доступно только владельцу вещи или арендатору.");
        }
        return BookingDtoMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUserId(Long userId, State state) {

        return BookingDtoMapper.toBookingDto(
                bookingRepository.findAll(getSpecificationByState(state, UserType.BOOKER, userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, State state) {
        if (!itemRepository.existsByOwnerId(ownerId)) {
            throw new NotFoundException("Этот запрос имеет смысл для владельца хотя бы одной вещи.");
        }
        return BookingDtoMapper.toBookingDto(
                bookingRepository.findAll(getSpecificationByState(state, UserType.OWNER, ownerId))
        );
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    private Specification<Booking> getSpecificationByState(State state, UserType type, Long id) {

        return (root, query, criteriaBuilder) -> {

            ArrayList<Predicate> predicates = new ArrayList<>();

            if (type == UserType.BOOKER) {
                Join<Object, Object> bookerJoin = root.join("booker");
                predicates.add(criteriaBuilder.equal(bookerJoin.get("id"), id));

            } else if (type == UserType.OWNER) {
                Join<Object, Object> ownerJoin = root.join("item").join("owner");
                predicates.add(criteriaBuilder.equal(ownerJoin.get("id"), id));
            }

            if (state == State.CURRENT) {
                Predicate start = criteriaBuilder.le(root.get("start"), Instant.now().toEpochMilli());
                Predicate end = criteriaBuilder.ge(root.get("end"), Instant.now().toEpochMilli());
                predicates.add(criteriaBuilder.and(start, end));
            } else if (state == State.PAST) {
                predicates.add(criteriaBuilder.lt(root.get("end"), Instant.now().toEpochMilli()));
            } else if (state == State.FUTURE) {
                predicates.add(criteriaBuilder.gt(root.get("start"), Instant.now().toEpochMilli()));
            } else if (state == State.WAITING || state == State.REJECTED) {
                predicates.add(criteriaBuilder.equal(root.get("status"), state));
            }

            query.orderBy(criteriaBuilder.desc(root.get("start")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };


    }
}
