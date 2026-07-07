package net.javaguides.ems.security;

import org.springframework.security.oauth2.core.user.OAuth2User;

final class OAuth2Attributes {

  private OAuth2Attributes() {}

  static String getString(OAuth2User user, String key) {
    Object value = user.getAttributes().get(key);
    return value instanceof String string ? string : null;
  }
}
