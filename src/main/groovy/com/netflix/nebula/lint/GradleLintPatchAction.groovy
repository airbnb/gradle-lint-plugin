import static com.netflix.nebula.lint.FileType.Symlink
import static com.netflix.nebula.lint.PatchType.*
import static java.nio.file.Files.readSymbolicLink

    static determinePatchType(List<GradleLintFix> patchFixes) {
        if (patchFixes.size() == 1 && patchFixes.get(0) instanceof DeletesFile)
            return Delete
        else if (patchFixes.size() == 1 && patchFixes.get(0) instanceof CreatesFile) {
            return Create
        } else {
            return Update
        }
    }

    static readFileOrSymlink(File file, FileType type) {
        return type == Symlink ? [readSymbolicLink(file.toPath()).toString()] : file.readLines()
    }

    static diffHints(String path, PatchType patchType, FileType fileType) {
        def headers = ["diff --git a/$path b/$path"]
        switch (patchType) {
            case Create:
                headers += "new file mode ${fileType.mode}"
                break
            case Delete:
                headers += "deleted file mode ${fileType.mode}"
                break
            case Update:
                // no hint necessary
                break
        }
        return headers.collect { "|$it" }.join('\n')
    }

        fixes.groupBy { it.affectedFile }.each { file, fileFixes ->  // internal ordering of fixes per file is maintained (file order does not)
            def (individualFixes, maybeCombinedFixes) = fileFixes.split { it instanceof RequiresOwnPatchset }
            individualFixes.each { patchSets.add([it] as List<GradleLintFix>) }

            GradleLintFix last = null
            for (f in maybeCombinedFixes.sort { f1, f2 -> f1.from() <=> f2.from() ?: f1.to() <=> f2.to() ?: f1.changes() <=> f2.changes() }) {
                    curPatch = [f] as List<GradleLintFix>
        def lastPathDeleted = null
            def patchType = determinePatchType(patchFixes)

            def fileType = patchType == Create ? (patchFixes[0] as GradleLintCreateFile).fileType : FileType.fromFile(file)
            def emptyFile = file.exists() ? (lastPathDeleted == file.absolutePath || patchType == Create ||
                    readFileOrSymlink(file, fileType).size() == 0) : true
            def newlineAtEndOfOriginal = emptyFile ? false : fileType != Symlink && file.text[-1] == '\n'
            def lines = [''] // the extra empty line is so we don't have to do a bunch of zero-based conversions for line arithmetic
            if (!emptyFile) lines += readFileOrSymlink(file, fileType)

                    if (j == 0) {
                } else if (fix instanceof GradleLintInsertAfter && fix.afterLine == lines.size() - 1 && !newlineAtEndOfOriginal && !emptyFile) {
                    if (lastFix && lastLineOfContext == lines.size() && !newlineAtEndOfOriginal) {

            def diffHeader = """\
                ${diffHints(path, patchType, fileType)}
                |--- ${patchType == Create ? '/dev/null' : 'a/' + path}
                |+++ ${patchType == Delete ? '/dev/null' : 'b/' + path}
                |@@ -${emptyFile ? 0 : firstLineOfContext},$beforeLineCount +${afterLineCount == 0 ? 0 : firstLineOfContext},$afterLineCount @@
                |""".stripMargin()

            combinedPatch += diffHeader + patch.join('\n')

            lastPathDeleted = patchType == Delete ? file.absolutePath : null

enum PatchType {
    Update, Create, Delete
}