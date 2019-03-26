/**
 * Copyright (c) 2019 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.core.util;

import junit.framework.Assert;
import org.junit.Test;

public class FileHandleUtilsTest {

	@Test
	public void testNormalise() {
		Assert.assertEquals("test.txt", FileHandleUtils.normalise("./test.txt"));
		Assert.assertEquals("dir1/test.txt", FileHandleUtils.normalise("dir1/./test.txt"));
		Assert.assertEquals("test.txt", FileHandleUtils.normalise("./../test.txt"));
		Assert.assertEquals("dir2/test.txt", FileHandleUtils.normalise("dir/../dir2/test.txt"));
		Assert.assertEquals("dir3/test.txt", FileHandleUtils.normalise("dir/../dir2/../dir3/test.txt"));
		Assert.assertEquals("test.txt", FileHandleUtils.normalise("dir/dir2/../dir3/../../test.txt"));
		Assert.assertEquals("maps/tsx/t_obj_trees.tsx", FileHandleUtils.normalise("maps/objTemplates/../tsx/t_obj_trees.tsx"));
		Assert.assertEquals("maps/tsx/t_obj_trees.tsx", FileHandleUtils.normalise("maps/objTemplates/trees/../../tsx/t_obj_trees.tsx"));
	}
}
