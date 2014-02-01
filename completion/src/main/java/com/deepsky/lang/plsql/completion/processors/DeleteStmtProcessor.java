/*
 * Copyright (c) 2009,2014 Serhiy Kulyk
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *
 * SQL CODE ASSISTANT PLUG-IN FOR INTELLIJ IDEA IS PROVIDED BY SERHIY KULYK
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SERHIY KULYK BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.deepsky.lang.plsql.completion.processors;

import com.deepsky.lang.plsql.completion.SyntaxTreePath;
import com.deepsky.lang.plsql.completion.VariantsProvider;
import com.deepsky.lang.plsql.psi.NameFragmentRef;
import com.deepsky.lang.plsql.psi.SelectStatement;
import com.deepsky.lang.plsql.psi.TableAlias;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;

import java.util.ArrayList;
import java.util.List;

@SyntaxTreePath("/..#DELETE_COMMAND")
public class DeleteStmtProcessor extends CompletionBase {

    @SyntaxTreePath("/#DELETE #FROM 1#TABLE_ALIAS/#TABLE_REF #ALIAS_NAME//#C_MARKER")
    public void process$DeleteAliasName(C_Context context, ASTNode node) {
        // TODO - implement me
    }

    @SyntaxTreePath("/#DELETE #FROM #TABLE_ALIAS/#TABLE_REF/#C_MARKER")
    public void process$DeleteTabRef(C_Context ctx) {
        VariantsProvider provider = ctx.getProvider();
        final List<LookupElement> variants = new ArrayList<LookupElement>();
        variants.addAll(provider.collectTableNameVariants(ctx.getLookup()));

        for (LookupElement elem : variants) {
            ctx.getResultSet().withPrefixMatcher(ctx.getLookup()).addElement(elem);
        }
    }

    @SyntaxTreePath("/#DELETE #FROM 1$TableAlias #WHERE_CONDITION/..#VAR_REF/..2$NameFragmentRef/#C_MARKER")
    public void process$DeleteWhere(C_Context ctx, TableAlias t, NameFragmentRef nameRef) {
        VariantsProvider provider = ctx.getProvider();
        provider.collectColumnNames(t, ctx.getLookup(), false);
        // TODO - handle case when column with table alias
        final List<LookupElement> variants = new ArrayList<LookupElement>();
        variants.addAll(provider.takeCollectedLookups());

        for (LookupElement elem : variants) {
            ctx.getResultSet().withPrefixMatcher(ctx.getLookup()).addElement(elem);
        }
    }

    @SyntaxTreePath("/#DELETE #FROM 1$TableAlias #WHERE_CONDITION//..#SUBQUERY_EXPR//..2$SelectStatement/..#WHERE_CONDITION//..#VAR_REF/..3$NameFragmentRef/#C_MARKER")
    public void process$DeleteWithSubquey(C_Context ctx, TableAlias t, SelectStatement select, NameFragmentRef ref) {
        collectColumns(ctx, select, ref);
        VariantsProvider provider = ctx.getProvider();
        provider.collectColumnNames(t, ctx.getLookup(), false);

        // TODO - filter out columns already existing in column list
        final List<LookupElement> variants = new ArrayList<LookupElement>();
        variants.addAll(provider.takeCollectedLookups());

        for (LookupElement elem : variants) {
            ctx.getResultSet().withPrefixMatcher(ctx.getLookup()).addElement(elem);
        }
    }

}