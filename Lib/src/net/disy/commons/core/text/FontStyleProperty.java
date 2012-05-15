/**
 * Copyright (C) 2005, 2011 disy Informationssysteme GmbH and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package net.disy.commons.core.text;

public enum FontStyleProperty {

  BOLD("Bold"), ITALICS("Italics"); //$NON-NLS-1$ //$NON-NLS-2$

  private final String name;

  private FontStyleProperty(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}