/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.marshalling.spi.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.function.Function;

import org.wildfly.clustering.marshalling.spi.BooleanExternalizer;

/**
 * @author Paul Ferraro
 */
public class LinkedHashMapExternalizer extends MapExternalizer<LinkedHashMap<Object, Object>, Boolean> {

    public static final Function<Boolean, LinkedHashMap<Object, Object>> FACTORY = new Function<Boolean, LinkedHashMap<Object, Object>>() {
        @Override
        public LinkedHashMap<Object, Object> apply(Boolean accessOrder) {
            return new LinkedHashMap<>(16, 0.75f, accessOrder);
        }
    };

    public static final Function<LinkedHashMap<Object, Object>, Boolean> ACCESS_ORDER = new Function<LinkedHashMap<Object, Object>, Boolean>() {
        @Override
        public Boolean apply(LinkedHashMap<Object, Object> map) {
            Object insertOrder = new Object();
            Object accessOrder = new Object();
            map.put(insertOrder, null);
            map.put(accessOrder, null);
            // Access first inserted entry
            // If map uses access order, this element will move to the tail of the map
            map.get(insertOrder);
            Iterator<Object> keys = map.keySet().iterator();
            Object element = keys.next();
            while ((element != insertOrder) && (element != accessOrder)) {
                element = keys.next();
            }
            map.remove(insertOrder);
            map.remove(accessOrder);
            return element == accessOrder;
        }
    };

    public LinkedHashMapExternalizer() {
        super(LinkedHashMap.class, FACTORY, ACCESS_ORDER, new BooleanExternalizer<>(Boolean.class, Function.identity(), Function.identity()));
    }
}
