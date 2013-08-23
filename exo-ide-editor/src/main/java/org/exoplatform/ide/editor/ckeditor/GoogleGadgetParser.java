/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.editor.ckeditor;

public class GoogleGadgetParser {

    private static String prefixPattern = "<Content\\s+type\\s*=\\s*[\"']html[\"']\\s*>[\\s\\r\\n]*<!\\[CDATA\\[";

    private static String suffixPattern = "\\]\\]>[\\s\\r\\n]*<\\/Content\\s*>";

    /**
     * @param code
     *         - all gadget content
     * @return prefix of gadget from start to CDATA open tag "<![CDATA["
     */
    protected static native String getPrefix(String code) /*-{
        if (@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::hasContentSection(Ljava/lang/String;)(code)) {
            var prefixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::prefixPattern, "im");

            var codeWhithoutPrefix = code.split(prefixPattern)[1];
            var codeWhithoutPrefixStartPosition = code.indexOf(codeWhithoutPrefix);
            return code.substring(0, codeWhithoutPrefixStartPosition);
        }
        else {
            return "";
        }
    }-*/;

    /**
     * @param code
     *         - gadget content
     * @return Content section between Content-CDATA tags
     */
    protected static native String getContentSection(String code) /*-{
        if (@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::hasContentSection(Ljava/lang/String;)(code)) {
            var prefixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::prefixPattern, "im");
            var suffixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::suffixPattern, "im");

            var codeWhithoutPrefix = code.split(prefixPattern)[1];
            var suffixCodeStartPosition = codeWhithoutPrefix.search(suffixPattern);

            code = codeWhithoutPrefix.substring(0, suffixCodeStartPosition);
        }

        return code;
    }-*/;

    /**
     * @param code
     *         - all gadget content
     * @return suffix of gadget from end of Content section to end of file
     */
    protected static native String getSuffix(String code) /*-{
        if (@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::hasContentSection(Ljava/lang/String;)(code)) {
            var suffixCodeStartPosition = code.search(new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::suffixPattern,
                "im"));
            return code.substring(suffixCodeStartPosition);
        }
        else {
            return "";
        }
    }-*/;

    /**
     * @param code
     *         - gadget content
     * @return <b>true</b>
     */
    protected static native boolean hasContentSection(String code) /*-{
        var prefixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::prefixPattern, "im");
        var suffixPattern = new RegExp(@org.exoplatform.ide.editor.ckeditor.GoogleGadgetParser::suffixPattern, "im");
        var prefixCodeStartPosition = code.search(prefixPattern);
        var suffixCodeStartPosition = code.search(suffixPattern);

        return prefixPattern.test(code)
            && suffixPattern.test(code)
            && (prefixCodeStartPosition < suffixCodeStartPosition);
    }-*/;
}