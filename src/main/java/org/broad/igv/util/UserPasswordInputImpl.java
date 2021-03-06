/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2007-2015 Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.broad.igv.util;

import htsjdk.samtools.seekablestream.UserPasswordInput;
import org.broad.igv.ui.IGV;

/**
 * @author Jim Robinson
 * @date 10/4/11
 */
public class UserPasswordInputImpl implements UserPasswordInput {

    String host;
    String password;
    String user;

    public void setHost(String host) {
        this.host = host;
    }

    public boolean showDialog() {

        UserPasswordDialog dlg = new UserPasswordDialog(IGV.getInstance().getMainFrame(), user, host);
        dlg.setVisible(true);

        if (dlg.isCanceled()) {
            dlg.dispose();
            return false;
        } else {
            user = dlg.getUser();
            password = dlg.getPassword();
            dlg.dispose();
            return true;
        }

    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
