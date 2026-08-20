# Erzeugt aus icon.png die Ebenen eines adaptiven Android-Startsymbols.
#
# Die Vorlage bringt ein abgerundetes dunkles Quadrat mit weissem Rand mit.
# Android legt aber seine eigene Maske darueber, also muss beides weg: Der
# weisse Rand wird weggeschnitten, das dunkle Quadrat verschwindet optisch,
# weil die Hintergrundebene denselben Ton bekommt.

Add-Type -AssemblyName System.Drawing

# Beides relativ zum Projektstamm, damit das Skript einen Umzug des Ordners
# uebersteht. $PSScriptRoot ist tools\, eine Ebene darunter liegt das Projekt.
$projectDir = Split-Path -Parent $PSScriptRoot
$source     = Join-Path $projectDir 'icon.png'
$resDir     = Join-Path $projectDir 'app\src\main\res'

# Das dunkle Quadrat in der Vorlage, ohne den weissen Rand (ausgemessen).
# Um 6 px nach innen gerueckt: Genau auf der Kante liegen Mischpixel aus
# Dunkel und Weiss, die sonst als heller Saum stehen bleiben - im Vordergrund
# als Umriss, in der Silhouette als Strich.
$sx = 73; $sy = 72; $sw = 1107; $sh = 1113

# Eckenradius als Anteil der Kantenlaenge. Etwas grosszuegiger als gemessen
# (~237 px), damit vom weissen Rand sicher nichts stehen bleibt - was dabei
# zusaetzlich vom Dunkel abgeschnitten wird, sieht man nicht.
$radiusShare = 245.0 / $sw

# Anteil der Flaeche, den das Quadrat auf der Vordergrundebene einnimmt.
#
# Massgeblich ist nicht die Breite des Motivs, sondern sein am weitesten
# aussen liegender Punkt: der Gitarrenkorpus links unten. Der sitzt rund
# 46 % der Kantenlaenge vom Mittelpunkt entfernt, die runde Maske laesst aber
# nur 33 % zu. Daraus folgt 0.33 / 0.46 = 0.72 als Obergrenze; 0.70 laesst
# etwas Luft fuer Launcher mit knapperer Maske.
#
# Bei 0.80 wurden Gitarre und Globus sichtbar angeschnitten - am Geraet geprueft.
$foregroundShare = 0.70

# Ohne diese Pruefung schreibt das Skript bei fehlender Vorlage klaglos leere
# Dateien weiter - der Build laeuft dann durch und die Symbole sind zerstoert,
# ohne dass es auffaellt. Einmal passiert, danach eingebaut.
if (-not (Test-Path $source)) {
    throw "Vorlage nicht gefunden: $source"
}
try {
    $src = New-Object System.Drawing.Bitmap($source)
} catch {
    throw "Vorlage nicht lesbar: $source - $($_.Exception.Message)"
}
if ($src.Width -lt 512 -or $src.Height -lt 512) {
    throw "Vorlage zu klein: $($src.Width) x $($src.Height), mindestens 512 x 512 noetig"
}

function New-Canvas([int]$size) {
    $bmp = New-Object System.Drawing.Bitmap($size, $size,
        [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    return $bmp
}

function Get-Graphics($bmp) {
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode      = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.InterpolationMode  = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.PixelOffsetMode    = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    return $g
}

function New-RoundedPath([double]$x, [double]$y, [double]$side, [double]$r) {
    $p = New-Object System.Drawing.Drawing2D.GraphicsPath
    $d = 2.0 * $r
    $p.AddArc($x, $y, $d, $d, 180, 90)
    $p.AddArc($x + $side - $d, $y, $d, $d, 270, 90)
    $p.AddArc($x + $side - $d, $y + $side - $d, $d, $d, 0, 90)
    $p.AddArc($x, $y + $side - $d, $d, $d, 90, 90)
    $p.CloseFigure()
    return $p
}

# Zeichnet das Quadrat der Vorlage skaliert und mit abgerundeten Ecken.
function Draw-Artwork($g, [double]$offset, [double]$side) {
    $clip = New-RoundedPath $offset $offset $side ($side * $radiusShare)
    $g.SetClip($clip)
    $dest = New-Object System.Drawing.RectangleF($offset, $offset, $side, $side)
    $srcRect = New-Object System.Drawing.RectangleF($sx, $sy, $sw, $sh)
    $g.DrawImage($src, $dest, $srcRect, [System.Drawing.GraphicsUnit]::Pixel)
    $g.ResetClip()
    $clip.Dispose()
}

function Save-Foreground([int]$size, [string]$path) {
    $bmp = New-Canvas $size
    $g = Get-Graphics $bmp
    $g.Clear([System.Drawing.Color]::Transparent)
    $side = $size * $foregroundShare
    Draw-Artwork $g (($size - $side) / 2.0) $side
    $g.Dispose()
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    return $bmp
}

# Silhouette fuer die eingefaerbten Symbole ab Android 13: alles, was heller
# als der dunkle Grundton ist, wird deckend schwarz, der Rest durchsichtig.
function Save-Monochrome($foregroundBmp, [int]$size, [string]$path) {
    $scaled = New-Canvas $size
    $g = Get-Graphics $scaled
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.DrawImage($foregroundBmp, 0, 0, $size, $size)
    $g.Dispose()

    for ($y = 0; $y -lt $size; $y++) {
        for ($x = 0; $x -lt $size; $x++) {
            $c = $scaled.GetPixel($x, $y)
            if ($c.A -lt 24) {
                $scaled.SetPixel($x, $y, [System.Drawing.Color]::Transparent)
            }
            elseif (([int]$c.R + [int]$c.G + [int]$c.B) -gt 380) {
                $scaled.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($c.A, 0, 0, 0))
            }
            else {
                $scaled.SetPixel($x, $y, [System.Drawing.Color]::Transparent)
            }
        }
    }
    $scaled.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $scaled.Dispose()
}

# Das alte, nicht adaptive Symbol - fuer Stellen, die kein adaptives lesen.
function Save-Legacy([int]$size, [string]$path, [bool]$round) {
    $bmp = New-Canvas $size
    $g = Get-Graphics $bmp
    $g.Clear([System.Drawing.Color]::Transparent)
    if ($round) {
        # Erst das vollstaendige Bild in eine Hilfsflaeche, dann durch eine
        # Kreismaske kopieren. Direkt uebereinander geht nicht: Draw-Artwork
        # setzt seine eigene Beschneidung und hebt sie danach auf - die Ecken
        # des Quadrats stuenden sonst ueber den Kreis hinaus.
        $inner = New-Canvas $size
        $ig = Get-Graphics $inner
        $ig.Clear([System.Drawing.Color]::FromArgb(255, 39, 42, 47))
        $side = $size * 0.92
        Draw-Artwork $ig (($size - $side) / 2.0) $side
        $ig.Dispose()

        $circle = New-Object System.Drawing.Drawing2D.GraphicsPath
        $circle.AddEllipse(0, 0, $size, $size)
        $g.SetClip($circle)
        $g.DrawImage($inner, 0, 0, $size, $size)
        $g.ResetClip()
        $circle.Dispose()
        $inner.Dispose()
    }
    else {
        Draw-Artwork $g 0.0 ([double]$size)
    }
    $g.Dispose()
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

$densities = @(
    @{ name = 'mdpi';    adaptive = 108; legacy = 48  },
    @{ name = 'hdpi';    adaptive = 162; legacy = 72  },
    @{ name = 'xhdpi';   adaptive = 216; legacy = 96  },
    @{ name = 'xxhdpi';  adaptive = 324; legacy = 144 },
    @{ name = 'xxxhdpi'; adaptive = 432; legacy = 192 }
)

# Einmal in voller Groesse, daraus werden die kleineren Silhouetten skaliert.
$masterFg = Save-Foreground 432 (Join-Path $env:TEMP 'fg_master.png')

foreach ($d in $densities) {
    $dir = Join-Path $resDir ("mipmap-" + $d.name)
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir | Out-Null }

    $fg = Save-Foreground $d.adaptive (Join-Path $dir 'ic_launcher_foreground.png')
    $fg.Dispose()

    Save-Monochrome $masterFg $d.adaptive (Join-Path $dir 'ic_launcher_monochrome.png')

    Save-Legacy $d.legacy (Join-Path $dir 'ic_launcher.png') $false
    Save-Legacy $d.legacy (Join-Path $dir 'ic_launcher_round.png') $true

    "{0,-8} adaptiv {1,3} px, klassisch {2,3} px" -f $d.name, $d.adaptive, $d.legacy
}

$masterFg.Dispose()
$src.Dispose()
"fertig"
