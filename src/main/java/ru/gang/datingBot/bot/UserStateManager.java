package ru.gang.datingBot.bot;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления состояниями пользователей в процессе диалога с ботом
 */
public class UserStateManager {

  /**
   * Enum для отслеживания состояния разговора с пользователями
   */
  public enum UserState {
    NONE,
    WAITING_FOR_DESCRIPTION,
    WAITING_FOR_INTERESTS,
    WAITING_FOR_PHOTO,
    WAITING_FOR_AGE,
    WAITING_FOR_GENDER,
    WAITING_FOR_MIN_AGE,
    WAITING_FOR_MAX_AGE,
    WAITING_FOR_GENDER_PREFERENCE,
    WAITING_FOR_MEETING_MESSAGE,
    WAITING_FOR_MEETING_PHOTO,
    CHATTING // Добавлено состояние чата
  }
  
  // Карта для отслеживания текущего состояния каждого пользователя
  private final Map<Long, UserState> userStates = new HashMap<>();
  
  // Хранилище для кэширования результатов поиска
  private final Map<Long, java.util.List<ru.gang.datingBot.model.User>> nearbyUsersCache = new HashMap<>();
  private final Map<Long, Integer> currentUserIndexCache = new HashMap<>();
  
  // Хранилище временных данных для создания запроса на встречу
  private final Map<Long, String> meetingRequestMessages = new HashMap<>();
  private final Map<Long, String> meetingRequestPhotos = new HashMap<>();
  private final Map<Long, Long> meetingRequestTargets = new HashMap<>();
  
  // Хранилище настроек геолокации
  private final Map<Long, Integer> userLiveLocationDurations = new HashMap<>();
  private final Map<Long, Integer> userSearchRadius = new HashMap<>();
  
  // Хранилище информации о текущем чате
  private final Map<Long, Long> currentChatUser = new HashMap<>(); // chatId -> targetUserId
  private final Map<Long, Long> currentChatMeetingRequest = new HashMap<>(); // chatId -> meetingRequestId

  /**
   * Получить текущее состояние пользователя
   */
  public UserState getUserState(Long chatId) {
    return userStates.getOrDefault(chatId, UserState.NONE);
  }

  /**
   * Установить состояние пользователя
   */
  public void setUserState(Long chatId, UserState state) {
    userStates.put(chatId, state);
  }

  /**
   * Проверить, находится ли пользователь в указанном состоянии
   */
  public boolean isUserInState(Long chatId, UserState state) {
    return getUserState(chatId) == state;
  }

  /**
   * Начать чат между пользователями
   */
  public void startChatting(Long chatId, Long targetUserId, Long meetingRequestId) {
    setUserState(chatId, UserState.CHATTING);
    currentChatUser.put(chatId, targetUserId);
    currentChatMeetingRequest.put(chatId, meetingRequestId);
  }

  /**
   * Завершить текущий чат
   */
  public void endChatting(Long chatId) {
    setUserState(chatId, UserState.NONE);
    currentChatUser.remove(chatId);
    currentChatMeetingRequest.remove(chatId);
  }
  
  /**
   * Получить ID пользователя, с которым ведется чат
   */
  public Long getCurrentChatUser(Long chatId) {
    return currentChatUser.get(chatId);
  }
  
  /**
   * Получить ID запроса на встречу, связанного с чатом
   */
  public Long getCurrentChatMeetingRequest(Long chatId) {
    return currentChatMeetingRequest.get(chatId);
  }

  // Существующие методы для кэша поиска
  public void cacheNearbyUsers(Long chatId, java.util.List<ru.gang.datingBot.model.User> users) {
    nearbyUsersCache.put(chatId, users);
    currentUserIndexCache.put(chatId, 0); // Начинаем с первого пользователя
  }

  public java.util.List<ru.gang.datingBot.model.User> getNearbyUsersCache(Long chatId) {
    return nearbyUsersCache.get(chatId);
  }

  public Integer getCurrentUserIndex(Long chatId) {
    return currentUserIndexCache.get(chatId);
  }

  public void setCurrentUserIndex(Long chatId, Integer index) {
    currentUserIndexCache.put(chatId, index);
  }

  /**
   * Методы для управления запросами на встречу
   */
  public void saveMeetingRequestMessage(Long chatId, String message) {
    meetingRequestMessages.put(chatId, message);
  }

  public String getMeetingRequestMessage(Long chatId) {
    return meetingRequestMessages.get(chatId);
  }

  public void saveMeetingRequestPhoto(Long chatId, String photoFileId) {
    meetingRequestPhotos.put(chatId, photoFileId);
  }

  public String getMeetingRequestPhoto(Long chatId) {
    return meetingRequestPhotos.get(chatId);
  }

  public void saveMeetingRequestTarget(Long chatId, Long targetId) {
    meetingRequestTargets.put(chatId, targetId);
  }

  public Long getMeetingRequestTarget(Long chatId) {
    return meetingRequestTargets.get(chatId);
  }

  public void clearMeetingRequestData(Long chatId) {
    meetingRequestMessages.remove(chatId);
    meetingRequestPhotos.remove(chatId);
    meetingRequestTargets.remove(chatId);
  }

  /**
   * Методы для управления настройками геолокации
   */
  public void saveLocationDuration(Long chatId, Integer hours) {
    userLiveLocationDurations.put(chatId, hours);
  }

  public Integer getLocationDuration(Long chatId) {
    return userLiveLocationDurations.get(chatId);
  }

  public void saveSearchRadius(Long chatId, Integer radius) {
    userSearchRadius.put(chatId, radius);
  }

  public Integer getSearchRadius(Long chatId) {
    return userSearchRadius.get(chatId);
  }

  public boolean hasLocationSettings(Long chatId) {
    return userLiveLocationDurations.containsKey(chatId) && userSearchRadius.containsKey(chatId);
  }
}