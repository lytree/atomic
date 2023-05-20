package top.lytree.bean;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

public class MemberUtils {
    private static final int ACCESS_TEST = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

    /**
     * Default access superclass workaround.
     * <p>
     * When a {@code public} class has a default access superclass with {@code public} members,
     * these members are accessible. Calling them from compiled code works fine.
     * Unfortunately, on some JVMs, using reflection to invoke these members
     * seems to (wrongly) prevent access even when the modifier is {@code public}.
     * Calling {@code setAccessible(true)} solves the problem but will only work from
     * sufficiently privileged code. Better workarounds would be gratefully
     * accepted.
     *
     * @param obj the AccessibleObject to set as accessible
     * @return a boolean indicating whether the accessibility of the object was set to true.
     */
    static <T extends AccessibleObject> T setAccessibleWorkaround(final T obj) {
        if (obj == null || obj.isAccessible()) {
            return obj;
        }
        final Member m = (Member) obj;
        if (!obj.isAccessible() && isPublic(m) && isPackageAccess(m.getDeclaringClass().getModifiers())) {
            try {
                obj.setAccessible(true);
                return obj;
            } catch (final SecurityException ignored) {
                // ignore in favor of subsequent IllegalAccessException
            }
        }
        return obj;
    }

    /**
     * Tests whether a given set of modifiers implies package access.
     *
     * @param modifiers to test
     * @return {@code true} unless {@code package}/{@code protected}/{@code private} modifier detected
     */
    static boolean isPackageAccess(final int modifiers) {
        return (modifiers & ACCESS_TEST) == 0;
    }

    /**
     * Tests whether a {@link Member} is public.
     *
     * @param member Member to test
     * @return {@code true} if {@code m} is public
     */
    static boolean isPublic(final Member member) {
        return member != null && Modifier.isPublic(member.getModifiers());
    }

    /**
     * Tests whether a {@link Member} is static.
     *
     * @param member Member to test
     * @return {@code true} if {@code m} is static
     */
    static boolean isStatic(final Member member) {
        return member != null && Modifier.isStatic(member.getModifiers());
    }

    /**
     * Tests whether a {@link Member} is accessible.
     *
     * @param member Member to test
     * @return {@code true} if {@code m} is accessible
     */
    static boolean isAccessible(final Member member) {
        return isPublic(member) && !member.isSynthetic();
    }

}
