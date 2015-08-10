
required.packages <- c("reshape2", "ggplot2", "data.table", "Cairo",
                "snowfall", "gplots", "foreach", "doParallel", "visreg", "stringr");
missing.packages <- function(required) {
        return(required[
                !(required %in% installed.packages()[,"Package"])]);
}
new.packages <- missing.packages(required.packages)
if (length(new.packages)) {
        install.packages(new.packages, lib="/usr/lib64/RRO-3.2.0/R-3.2.0/lib/R/library", Ncpu=2);
}

if (length(missing.packages(required.packages))) {
        warning('Some packages not installed');
        quit(1);
}
