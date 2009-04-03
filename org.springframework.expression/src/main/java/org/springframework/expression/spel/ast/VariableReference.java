/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel.ast;

import org.antlr.runtime.Token;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.ExpressionState;
import org.springframework.expression.spel.SpelException;
import org.springframework.expression.spel.SpelMessages;

/**
 * Represents a variable reference, eg. #someVar. Note this is different to a *local* variable like $someVar
 * 
 * @author Andy Clement
 * @since 3.0
 */
public class VariableReference extends SpelNodeImpl {

	// Well known variables:
	private final static String THIS = "this";  // currently active context object
	private final static String ROOT = "root";  // root context object

	private final String name;


	public VariableReference(Token payload) {
		super(payload);
		this.name = payload.getText();
	}


	@Override
	public TypedValue getValueInternal(ExpressionState state) throws SpelException {
		if (this.name.equals(THIS)) {
			return state.getActiveContextObject();
		}
		if (this.name.equals(ROOT)) {
			return state.getRootContextObject();
		}
		TypedValue result = state.lookupVariable(this.name);
		if (result == null) {
			throw new SpelException(getCharPositionInLine(), SpelMessages.VARIABLE_NOT_FOUND, this.name);
		}
		return result;
	}

	@Override
	public void setValue(ExpressionState state, Object value) throws SpelException {
		state.setVariable(this.name, value);
	}

	@Override
	public String toStringAST() {
		return "#" + this.name;
	}

	@Override
	public boolean isWritable(ExpressionState expressionState) throws SpelException {
		return !(this.name.equals(THIS) || this.name.equals(ROOT));
	}

}
