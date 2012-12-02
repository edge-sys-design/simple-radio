<?php

final class SimpleRadioLinters extends PhutilLintEngine {
  public function buildLinters() {
    $linters = parent::buildLinters();
    $paths = $this->getPaths();

    foreach ($paths as $key => $path) {
      if (!$this->pathExists($path)) {
        unset($paths[$key]);
      }
    }

    $linters[] = id(new ArcanistScalaSBTLinter())
      ->setPaths(preg_grep('/\.scala$/', $paths));

    $linters[] = id(new ArcanistFilenameLinter())->setPaths($paths);

    $linters[] = id(new ArcanistTextLinter())
      ->setPaths($paths)
      ->setMaxLineLength(100);

    $linters[] = id(new ArcanistPhutilLibraryLinter())->setPaths($paths);

    return $linters;
  }
}
