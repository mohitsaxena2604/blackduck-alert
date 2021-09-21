package com.synopsys.integration.alert.processing;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.synopsys.integration.alert.common.persistence.accessor.NotificationAccessor;
import com.synopsys.integration.alert.common.rest.model.AlertNotificationModel;
import com.synopsys.integration.alert.common.rest.model.AlertPagedModel;

public class MockNotificationAccessor implements NotificationAccessor {
    ArrayList<AlertNotificationModel> alertNotificationModels;
    BiPredicate<AlertNotificationModel, OffsetDateTime> createdBeforePredicate = (notification, date) -> notification.getCreatedAt().isBefore(date);

    public MockNotificationAccessor(List<AlertNotificationModel> alertNotificationModels) {
        this.alertNotificationModels = new ArrayList<>(alertNotificationModels);
    }

    @Override
    public List<AlertNotificationModel> saveAllNotifications(Collection<AlertNotificationModel> notifications) {
        alertNotificationModels.addAll(notifications);
        return new ArrayList<>(notifications);
    }

    @Override
    public List<AlertNotificationModel> findByIds(List<Long> notificationIds) {
        List<AlertNotificationModel> notifications = new LinkedList<>();
        for (Long notificationId : notificationIds) {
            alertNotificationModels.stream()
                .filter(notification -> notification.getId().equals(notificationId))
                .forEach(notifications::add);
        }
        return notifications;
    }

    @Override
    public Optional<AlertNotificationModel> findById(Long notificationId) {
        return alertNotificationModels.stream()
            .filter(notification -> notification.getId().equals(notificationId))
            .findFirst();
    }

    @Override
    public List<AlertNotificationModel> findByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        return alertNotificationModels.stream()
            .filter(notification -> notification.getCreatedAt().isAfter(startDate) && notification.getCreatedAt().isBefore(endDate))
            .collect(Collectors.toList());
    }

    @Override
    public List<AlertNotificationModel> findByCreatedAtBefore(OffsetDateTime date) {
        return alertNotificationModels.stream()
            .filter(notification -> notification.getCreatedAt().isAfter(date))
            .collect(Collectors.toList());
    }

    @Override
    public List<AlertNotificationModel> findByCreatedAtBeforeDayOffset(int dayOffset) {
        return null;
    }

    @Override
    public AlertPagedModel<AlertNotificationModel> getFirstPageOfNotificationsNotProcessed(int pageSize) {
        ArrayList<AlertNotificationModel> notificationsNotProcessed = new ArrayList<>();
        for (AlertNotificationModel notification : alertNotificationModels) {
            if (!notification.getProcessed()) {
                notificationsNotProcessed.add(notification);
            }
        }
        Page<AlertNotificationModel> pageOfNotifications;
        if (notificationsNotProcessed.size() > 0) {
            pageOfNotifications = new PageImpl<>(notificationsNotProcessed);
        } else {
            pageOfNotifications = Page.empty();
        }
        return new AlertPagedModel<>(pageOfNotifications.getTotalPages(), pageOfNotifications.getNumber(), pageOfNotifications.getSize(), pageOfNotifications.getContent());
    }

    @Override
    public void setNotificationsProcessed(List<AlertNotificationModel> notifications) {
        for (AlertNotificationModel notification : notifications) {
            AlertNotificationModel updatedNotification = createProcessedAlertNotificationModel(notification);
            int index = alertNotificationModels.indexOf(notification);
            alertNotificationModels.set(index, updatedNotification);
        }
    }

    @Override
    public void setNotificationsProcessedById(Set<Long> notificationIds) {
        for (AlertNotificationModel notification : alertNotificationModels) {
            AlertNotificationModel updatedNotification = createProcessedAlertNotificationModel(notification);
            int index = alertNotificationModels.indexOf(notification);
            alertNotificationModels.set(index, updatedNotification);
        }
    }

    @Override
    public void deleteNotification(AlertNotificationModel notification) {
        alertNotificationModels.remove(notification);
    }

    @Override
    public int deleteNotificationsCreatedBefore(OffsetDateTime date) {
        return 0;
    }

    @Override
    public void deleteNotificationsForRemoval(int pageSize) {
        List<AlertNotificationModel> notificationsToRemove = alertNotificationModels.stream()
            .filter(AlertNotificationModel::getRemove)
            .collect(Collectors.toList());
        int count = notificationsToRemove.size() < pageSize ? notificationsToRemove.size() : pageSize;
        for (int index = 0; index < count; index++) {
            AlertNotificationModel savedModel = notificationsToRemove.get(index);
            alertNotificationModels.remove(savedModel);
        }
    }

    @Override
    public boolean existsNotificationsToMarkForRemoval(OffsetDateTime date) {
        return alertNotificationModels.stream()
            .anyMatch(notification -> createdBeforePredicate.test(notification, date));
    }

    @Override
    public boolean existsNotificationsToRemove() {
        return alertNotificationModels.stream().anyMatch(AlertNotificationModel::getRemove);
    }

    @Override
    public int markNotificationsToRemove(OffsetDateTime createdBefore, int pageSize) {
        List<AlertNotificationModel> notificationsToRemove = alertNotificationModels.stream()
            .filter(notification -> !notification.getRemove() && notification.getCreatedAt().isBefore(createdBefore))
            .collect(Collectors.toList());
        int count = notificationsToRemove.size() < pageSize ? notificationsToRemove.size() : pageSize;
        for (int index = 0; index < count; index++) {
            AlertNotificationModel modelToUpdate = notificationsToRemove.get(index);
            alertNotificationModels.remove(modelToUpdate);
            alertNotificationModels.add(createRemoveAlertNotificationModel(modelToUpdate));
        }

        return count;
    }

    //AlertNotificationModel is immutable, this is a workaround for the unit test to set "processed" to true.
    private AlertNotificationModel createProcessedAlertNotificationModel(AlertNotificationModel alertNotificationModel) {
        return new AlertNotificationModel(alertNotificationModel.getId(),
            alertNotificationModel.getProviderConfigId(),
            alertNotificationModel.getProvider(),
            alertNotificationModel.getProviderConfigName(),
            alertNotificationModel.getNotificationType(),
            alertNotificationModel.getContent(),
            alertNotificationModel.getCreatedAt(),
            alertNotificationModel.getProviderCreationTime(),
            true,
            false);
    }

    private AlertNotificationModel createRemoveAlertNotificationModel(AlertNotificationModel alertNotificationModel) {
        return new AlertNotificationModel(alertNotificationModel.getId(),
            alertNotificationModel.getProviderConfigId(),
            alertNotificationModel.getProvider(),
            alertNotificationModel.getProviderConfigName(),
            alertNotificationModel.getNotificationType(),
            alertNotificationModel.getContent(),
            alertNotificationModel.getCreatedAt(),
            alertNotificationModel.getProviderCreationTime(),
            alertNotificationModel.getProcessed(),
            true);
    }

}
