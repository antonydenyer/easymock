/**
 * Copyright 2009-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.easymock.itests;

import org.easymock.MockType;
import org.easymock.internal.MocksControl;
import org.easymock.internal.matchers.Equals;
import org.osgi.framework.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.Manifest;

import static org.easymock.EasyMock.expect;

/**
 * Test that we still can mock interfaces without cglib and objenesis as
 * dependencies
 *
 * @author Henri Tremblay
 */
public class InterfaceOnlyTest extends OsgiBaseTest {

    @Override
    protected String[] getTestBundlesNames() {

        String easymockVersion = getEasyMockVersion();

        return new String[] { "org.easymock, easymock, " + easymockVersion };
    }

    @Override
    protected Manifest getManifest() {
        Manifest mf = super.getManifest();

        String imports = mf.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
        imports = imports.replace("org.easymock.internal,", "org.easymock.internal;poweruser=true,");
        imports = imports.replace("org.easymock.internal.matchers,",
                "org.easymock.internal.matchers;poweruser=true,");

        mf.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, imports);

        return mf;
    }

    public void testCanMock() throws IOException {
        Appendable mock = mock(Appendable.class);
        expect(mock.append("test")).andReturn(mock);
        replayAll();
        assertSame(mock, mock.append("test"));
        verifyAll();
    }

    public void testCanUseMatchers() {
        new Equals(new Object());
    }

    public void testCanUseInternal() {
        new MocksControl(MockType.DEFAULT);
    }

    public void testCannotMock() throws Exception {
        try {
            mock(ArrayList.class);
            fail("Should throw an exception due to a NoClassDefFoundError");
        } catch (RuntimeException e) {
            assertEquals("Class mocking requires to have objenesis library in the classpath", e
                    .getMessage());
            assertTrue(e.getCause() instanceof NoClassDefFoundError);
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
