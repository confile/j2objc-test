package majestella.core.prototype.navigation;

import java.util.Set;

/**
 * Provide information about all registered place tokens.
 */
public interface BPlaceTokenRegistry {

  /**
   * @return All registered place tokens.
   */
  Set<String> getAllPlaceTokens();
  
}
