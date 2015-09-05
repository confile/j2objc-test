package majestella.core.prototype.mvp;

/**
 * This is an interface replacement of {@link BAbstractViewWithUiHandlers}.
 * @author Dr. Michael Gorski
 *
 */
public interface BViewWithUiHandlers<T extends BUiHandlers> extends BBaseView, BHasUiHandlers<T> {
}
