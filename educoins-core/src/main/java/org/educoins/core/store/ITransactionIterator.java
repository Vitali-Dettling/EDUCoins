package org.educoins.core.store;

import org.educoins.core.Input;
import org.educoins.core.Output;
import org.jetbrains.annotations.NotNull;

public interface ITransactionIterator {

	Output previous(@NotNull Input startInput);
}
