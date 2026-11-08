package chat.buku.skate3;

import android.content.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Small runtime string layer while the programmatic launcher moves to Android resources. */
final class LauncherStrings {
    static final String ENGLISH = "en";
    static final String PORTUGUESE_BRAZIL = "pt-BR";

    private static final String PREFERENCES = "launcher-language";
    private static final String LANGUAGE = "language";
    private static final Map<String, String> PT = new HashMap<>();

    static {
        put("COMMUNITY MODS", "MODS DA COMUNIDADE");
        put("SUBMIT A MOD", "ENVIAR UM MOD");
        put("Made a character? Send it in for the Mod Store. Character mods are currently supported; every submission is reviewed before publication.",
            "Criou um personagem? Envie para a Loja de Mods. No momento, aceitamos mods de personagens. Todos os envios passam por análise antes da publicação.");
        put("What to prepare", "O que preparar");
        put("1. A ZIP containing base.obj and texture_diffuse.png.\n2. A PNG or JPG preview.\n3. Your creator name, credit link, and redistribution permission.\n4. The app version, tested devices, and any known animation or board-contact problems.",
            "1. Um ZIP com base.obj e texture_diffuse.png.\n2. Uma prévia em PNG ou JPG.\n3. Seu nome de criador, link para os créditos e permissão de redistribuição.\n4. A versão do aplicativo, os dispositivos testados e problemas conhecidos de animação ou contato com o skate.");
        put("Only send work you created or have permission to share. Never attach an ISO, XEX, Title Update, save, or extracted retail game files.",
            "Envie apenas trabalhos que você criou ou tem permissão para compartilhar. Nunca anexe ISO, XEX, Title Update, save ou arquivos extraídos do jogo comercial.");
        put("The form opens on GitHub and requires an account. Submissions are public. You choose the attachments and press Submit on GitHub; this app does not upload files or publish a mod automatically.",
            "O formulário abre no GitHub e exige uma conta. Os envios são públicos. Você escolhe os anexos e confirma o envio no GitHub. Este aplicativo não envia arquivos nem publica mods automaticamente.");
        put("OPEN SUBMISSION FORM", "ABRIR FORMULÁRIO DE ENVIO");
        put("MODEL GUIDE AND SEIYU EXAMPLE", "GUIA DO MODELO E EXEMPLO DO SEIYU");
        put("BACK TO LAUNCHER", "VOLTAR AO LAUNCHER");
        put("No browser available", "Nenhum navegador disponível");
        put("Install a browser or copy this link to open it elsewhere.",
            "Instale um navegador ou copie este link para abri-lo em outro lugar.");
        put("Copy link", "Copiar link");
        put("Link copied.", "Link copiado.");
        put("PHONE-ONLY INSTALLER", "INSTALADOR SEM COMPUTADOR");
        put("No computer required. Select an Xbox 360 ISO that you dumped from your own Skate 3 copy. The game stays on this device.",
            "Nenhum computador é necessário. Selecione uma ISO de Xbox 360 extraída da sua própria cópia de Skate 3. O jogo permanece neste dispositivo.");
        put("Checking installation...", "Verificando a instalação...");
        put("CHECK FOR APP UPDATES", "PROCURAR ATUALIZAÇÕES");
        put("CHECKING FOR UPDATES...", "PROCURANDO ATUALIZAÇÕES...");
        put("APP UP TO DATE", "APLICATIVO ATUALIZADO");
        put("BUG REPORT / DEVICE DIAGNOSTICS", "RELATAR BUG / DIAGNÓSTICO DO DISPOSITIVO");
        put("Requires Android 13+, ARM64, Vulkan, and about 8 GB free after the ISO is already on your device or USB drive. Touch controls are included.",
            "Requer Android 13+, ARM64, Vulkan e cerca de 8 GB livres além da ISO no dispositivo ou unidade USB. Controles de toque estão incluídos.");
        put("DEVICE NOT SUPPORTED", "DISPOSITIVO NÃO COMPATÍVEL");
        put("This build requires a 64-bit ARM Android device.", "Esta versão requer um dispositivo Android ARM de 64 bits.");
        put("This build requires Android 13 or newer.", "Esta versão requer Android 13 ou mais recente.");
        put("This device does not report the required Vulkan support.", "Este dispositivo não informa o suporte Vulkan necessário.");
        put("CLOSE", "FECHAR");
        put("READY TO SKATE", "PRONTO PARA ANDAR DE SKATE");
        put("PLAY SKATE 3", "JOGAR SKATE 3");
        put("REPAIR OR REINSTALL", "REPARAR OU REINSTALAR");
        put("VIEW SETUP LOG", "VER REGISTRO DA INSTALAÇÃO");
        put("VIEW LAST SETUP LOG", "VER ÚLTIMO REGISTRO");
        put("GAME EXTRACTED", "JOGO EXTRAÍDO");
        put("Finish by downloading the verified 1.7 MB Title Update 3, or select the package yourself.",
            "Finalize baixando a Title Update 3 verificada de 1,7 MB ou selecione o pacote manualmente.");
        put("FINISH SETUP AUTOMATICALLY", "FINALIZAR INSTALAÇÃO AUTOMATICAMENTE");
        put("SELECT TITLE UPDATE FILE", "SELECIONAR ARQUIVO DA TITLE UPDATE");
        put("START OVER", "RECOMEÇAR");
        put("ONE FILE NEEDED", "FALTA UM ARQUIVO");
        put("SELECT MY SKATE 3 ISO", "SELECIONAR MINHA ISO DE SKATE 3");
        put("INSPECTING ISO", "ANALISANDO A ISO");
        put("EXTRACTING GAME", "EXTRAINDO O JOGO");
        put("INSTALLING TITLE UPDATE 3", "INSTALANDO TITLE UPDATE 3");
        put("VERIFYING TITLE UPDATE 3", "VERIFICANDO TITLE UPDATE 3");
        put("Downloading and verifying 1.7 MB...", "Baixando e verificando 1,7 MB...");
        put("The Title Update server is temporarily unavailable. The app tried 3 times. Try again later, or use Select Title Update File.",
            "O servidor da Title Update está temporariamente indisponível. O aplicativo tentou 3 vezes. Tente novamente mais tarde ou use Selecionar Arquivo da Title Update.");
        put("Title Update download was interrupted.", "O download da Title Update foi interrompido.");
        put("INSTALLATION COMPLETE", "INSTALAÇÃO CONCLUÍDA");
        put("Your files were verified and stayed on this device.", "Seus arquivos foram verificados e permaneceram neste dispositivo.");
        put("Game installation needs repair.", "A instalação do jogo precisa ser reparada.");
        put("You already have the newest build.", "Você já possui a versão mais recente.");
        put("DOWNLOADING APP UPDATE", "BAIXANDO ATUALIZAÇÃO DO APLICATIVO");
        put("UPDATE VERIFIED", "ATUALIZAÇÃO VERIFICADA");
        put("Android will now open the installer. Your game files stay in place.",
            "O Android abrirá o instalador. Os arquivos do jogo permanecerão no lugar.");
        put("Allow installs from Skate 3, then return here.", "Permita instalações pelo Skate 3 e depois volte aqui.");
        put("Later", "Depois");
        put("Download update", "Baixar atualização");
        put("The app will verify the download, then Android will ask you to tap Install. Your game files and settings stay in place.",
            "O aplicativo verificará o download e o Android pedirá que você toque em Instalar. Os arquivos e configurações do jogo permanecerão no lugar.");
        put("CHARACTERS  •  SEIYU INCLUDED", "PERSONAGENS  •  SEIYU INCLUÍDO");
        put("CHARACTERS  •  RESTORE SEIYU", "PERSONAGENS  •  RESTAURAR SEIYU");
        put("PREPARING SEIYU", "PREPARANDO SEIYU");
        put("Installing the included playable penguin...", "Instalando o pinguim jogável incluído...");
        put("ADVANCED OPTIONS", "OPÇÕES AVANÇADAS");
        put("ADVANCED OPTIONS  •  CUSTOM GPU DRIVER ACTIVE", "OPÇÕES AVANÇADAS  •  DRIVER DE GPU PERSONALIZADO ATIVO");
        put("CLEANING UP", "LIMPANDO");
        put("Removing this app's installed copy...", "Removendo a cópia instalada por este aplicativo...");
        put("Setup needs attention", "A instalação precisa de atenção");
        put("Your original ISO was not changed.", "Sua ISO original não foi alterada.");
        put("Repair or reinstall?", "Reparar ou reinstalar?");
        put("This removes the extracted game files from this app, then lets you select your ISO again. Your original ISO is not changed.",
            "Isso remove os arquivos extraídos pelo aplicativo e permite selecionar a ISO novamente. Sua ISO original não será alterada.");
        put("Discard partial setup?", "Descartar instalação parcial?");
        put("Only the incomplete copy created by this installer will be removed. Your original ISO is not changed.",
            "Somente a cópia incompleta criada pelo instalador será removida. Sua ISO original não será alterada.");
        put("Cancel", "Cancelar");
        put("Continue", "Continuar");
        put("Start over", "Recomeçar");
        put("Setup log", "Registro da instalação");
        put("No setup log has been written yet.", "Nenhum registro de instalação foi criado ainda.");
        put("Launcher language", "Idioma do launcher");
        put("Language changed. The launcher will restart.", "Idioma alterado. O launcher será reiniciado.");
        put("selected file", "arquivo selecionado");
        put("Open Buku313 on GitHub", "Abrir Buku313 no GitHub");
        put("Report a developer-build bug", "Relatar um bug da versão de desenvolvimento");
        put("GitHub will open with this device's safe technical details already filled in. The same details will be copied so you can paste them if the browser removes a field.\n\nNo ISO, game file, save, account name, or private path is included.",
            "O GitHub abrirá com os detalhes técnicos seguros deste dispositivo já preenchidos. Os mesmos detalhes serão copiados para você colar caso o navegador remova algum campo.\n\nNenhuma ISO, arquivo do jogo, save, nome de conta ou caminho privado será incluído.");
        put("Copy only", "Somente copiar");
        put("Open GitHub", "Abrir GitHub");
        put("Device diagnostics copied.", "Diagnóstico do dispositivo copiado.");
        put("No browser is available. The diagnostics are copied.", "Nenhum navegador está disponível. O diagnóstico foi copiado.");

        // Handheld setup (launcher) and the bottom-screen panel.
        put("HANDHELD SETUP", "CONFIGURAÇÃO DO PORTÁTIL");
        put("Handheld setup", "Configuração do portátil");
        put("GRAPHICS PRESET", "PREDEFINIÇÃO GRÁFICA");
        put("CONTROLLER LAYOUT", "LAYOUT DO CONTROLE");
        put("BOTTOM SCREEN PANEL", "PAINEL DA TELA INFERIOR");
        put("Detected device", "Dispositivo detectado");
        put("Graphics presets take effect the next time you press PLAY SKATE 3. Controller layouts apply immediately and can also be changed from the bottom screen while playing.",
            "As predefinições gráficas passam a valer na próxima vez que você tocar em JOGAR SKATE 3. Os layouts de controle são aplicados na hora e também podem ser alterados pela tela inferior durante o jogo.");
        put("Auto (recommended for this device)", "Automático (recomendado para este dispositivo)");
        put("Performance (288p, coolest)", "Desempenho (288p, mais frio)");
        put("Balanced (540p, cooler than Quality)", "Equilibrado (540p, mais frio que Qualidade)");
        put("Quality (720p, full materials)", "Qualidade (720p, materiais completos)");
        put("Auto (dual-screen handhelds only)", "Automático (somente portáteis com duas telas)");
        put("On whenever a second screen exists", "Ligado sempre que houver uma segunda tela");
        put("Off", "Desligado");
        put("Standard", "Padrão");
        put("Nintendo-style face buttons", "Botões no estilo Nintendo");
        put("Southpaw (sticks swapped)", "Canhoto (analógicos trocados)");
        put("Southpaw + Nintendo-style", "Canhoto + estilo Nintendo");
        put("Buttons and sticks exactly as labelled on an Xbox-layout pad.",
            "Botões e analógicos exatamente como indicados em um controle no layout do Xbox.");
        put("Swaps A with B and X with Y. Use this if your handheld is set to a Nintendo layout, or if A on your device should act as B.",
            "Troca A com B e X com Y. Use se o seu portátil estiver no layout Nintendo ou se o A do seu dispositivo deve funcionar como B.");
        put("The left stick does Flick-It board tricks and the right stick steers.",
            "O analógico esquerdo faz as manobras Flick-It e o direito controla a direção.");
        put("Swapped sticks and swapped face buttons together.",
            "Analógicos e botões frontais trocados ao mesmo tempo.");
        put("No device-specific recommendation; Auto keeps the memory-based default.",
            "Sem recomendação específica para este dispositivo; o modo Automático mantém o padrão baseado na memória.");
        put("Auto picks", "O Automático escolhe");
        put("SELECTED", "SELECIONADO");
        put("Close", "Fechar");
        put("PAUSE", "PAUSAR");
        put("RECOMP MENU", "MENU DO RECOMP");
        put("BACK", "VOLTAR");
        put("TRICKS", "MANOBRAS");
        put("CONTROLS", "CONTROLES");
        put("SETUP", "AJUSTES");
        put("Controls", "Controles");
        put("Graphics", "Gráficos");
        put("The trick list could not be loaded.", "Não foi possível carregar a lista de manobras.");
        put("Applies instantly to the built-in controls and any connected gamepad.",
            "Aplica na hora aos controles integrados e a qualquer controle conectado.");
        put("TOUCH CONTROLS ON GAME SCREEN", "CONTROLES DE TOQUE NA TELA DO JOGO");
        put("TOUCH BUTTON ON GAME SCREEN", "BOTÃO TOUCH NA TELA DO JOGO");
        put("ON", "LIGADO");
        put("OFF", "DESLIGADO");
        put("SHOWN", "VISÍVEL");
        put("HIDDEN", "OCULTO");
        put("DIM THIS SCREEN", "ESCURECER ESTA TELA");
        put("CLOSE PANEL UNTIL NEXT LAUNCH", "FECHAR PAINEL ATÉ A PRÓXIMA ABERTURA");
        put("Graphics changes need a restart. Change them in the launcher under HANDHELD SETUP.",
            "Mudanças gráficas exigem reiniciar. Altere no launcher em CONFIGURAÇÃO DO PORTÁTIL.");
    }

    private LauncherStrings() {}

    static boolean isPortuguese(Context context) {
        return PORTUGUESE_BRAZIL.equals(language(context));
    }

    static String language(Context context) {
        String saved = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getString(LANGUAGE, null);
        if (saved != null) return saved;
        return "pt".equalsIgnoreCase(Locale.getDefault().getLanguage())
            ? PORTUGUESE_BRAZIL : ENGLISH;
    }

    static void setLanguage(Context context, String language) {
        String selected = PORTUGUESE_BRAZIL.equals(language)
            ? PORTUGUESE_BRAZIL : ENGLISH;
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit().putString(LANGUAGE, selected).apply();
    }

    static String languageButton(Context context) {
        return isPortuguese(context) ? "🇺🇸 English" : "🇧🇷 Português";
    }

    static String text(Context context, String english) {
        if (!isPortuguese(context) || english == null || english.isEmpty()) return english;
        String exact = PT.get(english);
        if (exact != null) return exact;

        if (english.startsWith("UPDATE TO ")) {
            return "ATUALIZAR PARA " + english.substring("UPDATE TO ".length());
        }
        if (english.startsWith("Game files: ")) {
            return "Arquivos do jogo: " + english.substring("Game files: ".length());
        }
        if (english.contains("\nGame files: ")) {
            return english.replace("\nGame files: ", "\nArquivos do jogo: ");
        }
        if (english.startsWith("Select your Skate 3 Xbox 360 ISO.")) {
            return english.replace(
                "Select your Skate 3 Xbox 360 ISO. It can be in Downloads, on an SD card, or on a connected USB drive.",
                "Selecione sua ISO de Skate 3 para Xbox 360. Ela pode estar em Downloads, em um cartão SD ou em uma unidade USB conectada.")
                .replace("Free space: ", "Espaço livre: ");
        }
        if (english.startsWith("Checking ")) return "Verificando " + english.substring(9);
        if (english.startsWith("Reading ")) return "Lendo " + english.substring(8);
        if (english.startsWith("Starting ")) return "Iniciando " + english.substring(9);
        if (english.startsWith("Downloading Skate 3 ")) {
            return "Baixando Skate 3 " + english.substring("Downloading Skate 3 ".length());
        }
        if (english.startsWith("Retrying Title Update download (")) {
            return english.replace("Retrying Title Update download (",
                "Tentando baixar a Title Update novamente (")
                .replace(" of ", " de ");
        }
        if (english.startsWith("Title Update setup stopped: ")) {
            return "A instalação da Title Update foi interrompida: " +
                text(context, english.substring("Title Update setup stopped: ".length()));
        }
        if (english.startsWith("Setup stopped: ")) {
            return "A instalação foi interrompida: " +
                text(context, english.substring("Setup stopped: ".length()));
        }
        if (english.contains(" of ")) return english.replace(" of ", " de ");
        return english;
    }

    private static void put(String english, String portuguese) {
        PT.put(english, portuguese);
    }
}
