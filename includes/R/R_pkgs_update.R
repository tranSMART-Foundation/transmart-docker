update.packages(lib.loc = "/usr/lib64/RRO-3.2.0/R-3.2.0/lib/R/library", ask = FALSE, checkBuilt = TRUE)

source("http://bioconductor.org/biocLite.R")
required.packages <- c("WGCNA", "impute", "multtest", "CGHbase", "edgeR", "snpStats",
  "preprocessCore", "GO.db", "AnnotationDbi");
for (n in required.packages)
{
        biocUpdatePackages(n, ask=FALSE)
}
