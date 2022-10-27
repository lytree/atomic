/*
 * Copyright (C) 2009 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package top.lytree.base;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import top.lytree.lang.StringUtils;
import top.lytree.pattern.CharMatcher;
import top.lytree.pattern.CommonPattern;
import top.lytree.bean.Enums;
import top.lytree.pattern.JdkPattern;
import top.lytree.pattern.PatternCompiler;


/**
 * Methods factored out so that they can be emulated differently in GWT.
 *
 * @author Jesse Wilson
 */


final public class Platform {

    private static final PatternCompiler patternCompiler = loadPatternCompiler();

    private Platform() {
    }

    /**
     * Calls {@link System#nanoTime()}.
     */
    @SuppressWarnings("GoodTime") // reading system time without TimeSource
    public static long systemNanoTime() {
        return System.nanoTime();
    }

    public static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
        return matcher.precomputedInternal();
    }


    public static CommonPattern compilePattern(String pattern) {
        Assert.notNull(pattern);
        return patternCompiler.compile(pattern);
    }

    public static boolean patternCompilerIsPcreLike() {
        return patternCompiler.isPcreLike();
    }

    private static PatternCompiler loadPatternCompiler() {
        return new JdkPatternCompiler();
    }

    private static final class JdkPatternCompiler implements PatternCompiler {

        @Override
        public CommonPattern compile(String pattern) {
            return new JdkPattern(Pattern.compile(pattern));
        }

        @Override
        public boolean isPcreLike() {
            return true;
        }
    }


}
